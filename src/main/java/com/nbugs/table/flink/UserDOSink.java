package com.nbugs.table.flink;

import com.alibaba.fastjson.JSONObject;
import com.nbugs.table.entity.StaticExampleDO;
import com.nbugs.table.entity.StaticUserDO;
import com.nbugs.table.event.MsgUpdateEvent;
import com.nbugs.table.manage.RedisManage;
import com.nbugs.table.mapper.StaticExampleDOMapper;
import com.nbugs.table.mapper.StaticNodeDOMapper;
import com.nbugs.table.mapper.StaticUserDOMapper;
import com.nbugs.table.util.MybatisSessionFactory;

import java.util.*;

import lombok.Synchronized;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDOSink extends RichSinkFunction<String> {

    private static final long serialVersionUID = -2011080637808744038L;
    private static final Logger log = LoggerFactory.getLogger(UserDOSink.class);
    private static KafkaProducer<String, String> kafkaProducer;

    private RedisManage redisManage;
//    private Properties prop;
//
//    public UserDOSink(RedisManage redisManage, Properties properties) {
//        this.redisManage = redisManage;
//        this.prop = properties;
//    }

    public UserDOSink(RedisManage redisManage) {
        this.redisManage = redisManage;
    }

    @Override
    public void invoke(String value, Context context) throws Exception {
        doUpdate(value);
        super.invoke(value, context);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        MybatisSessionFactory.openSession();
    }

    @Override
    public void close() throws Exception {
        MybatisSessionFactory.close();
        super.close();
    }

    /**
     * 更新数据的核心逻辑
     */
    public void doUpdate(String value) {
        MsgUpdateEvent event;
        try {
            event = JSONObject.parseObject(value, MsgUpdateEvent.class);
        } catch (Exception e) {
            return;
        }

        StaticUserDOMapper userMapper = (StaticUserDOMapper) MybatisSessionFactory
            .getMapper(StaticUserDOMapper.class);
        StaticNodeDOMapper nodeMapper = (StaticNodeDOMapper) MybatisSessionFactory
            .getMapper(StaticNodeDOMapper.class);
        StaticExampleDOMapper exampleMapper = (StaticExampleDOMapper) MybatisSessionFactory
            .getMapper(StaticExampleDOMapper.class);

        Long id = event.getId();
        Long msgId = event.getMsgId();
        String key = null;
        StaticUserDO staticUserDO = new StaticUserDO();
        try {
            StaticExampleDO staticExampleDO = exampleMapper.queryById(msgId);
            staticUserDO = userMapper.queryById(id);
            if (staticExampleDO == null || staticUserDO == null || staticExampleDO.getStatus() != 1) {
                writeToKafka(staticUserDO, event);
                return;
            }
            String userId = staticUserDO.getUserId();
            key = msgId + "-" + userId;
            if (staticUserDO.getCountState() == 1) {
                log.info("already update this user:{}", userId);
                return;
            }
            //简单分布式锁
            if (!redisManage.setnx(key, "start", 60)) {
                return;
            }
            userMapper.updateReadStateByMsgIdAndUserId(msgId, userId, new Date());
            log.info("finish to do update user data...");
            countAllStructNode(msgId, userId, userMapper, nodeMapper);
            // 提交事务
            MybatisSessionFactory.commit();
            log.info("commit transaction success...");
            redisManage.set(key, "end", 60);
        } catch (Throwable e) {
            try {
                MybatisSessionFactory.rollback();
            } catch (Exception ex) {
                log.error("rollback error", ex);
            }
            try {
                redisManage.delete(key);
            } catch (Exception ex) {
                log.error("redisManage delete key error: {}", key, ex);
            }
            log.error("Exist Exception, sqlSession transaction rollback...");
            log.error(e.getMessage(), e);
            writeToKafka(staticUserDO, event);
        }
    }


    /**
     * 更新用户所属部门的已阅数量和阅读率
     */
    private void countAllStructNode(
        Long msgId,
        String userId,
        StaticUserDOMapper userMapper,
        StaticNodeDOMapper nodeMapper
    ) {
        //获取所有的用户所在部门
        List<String> ids = userMapper.findByMsgIdAndUserId(msgId, userId);
        Set<String> nodeIdSet = new HashSet<>(ids);
        int add = 0;
        do {
            List<String> parentIds = nodeMapper.findParentIdsByIds(ids);
            ids = new ArrayList<>();
            for (String parentId : parentIds) {
                if (parentId != null && parentId.length() > 0 && !nodeIdSet.contains(parentId)) {
                    ids.add(parentId);
                    nodeIdSet.add(parentId);
                }
            }
            add = ids.size();
        } while (add > 0);
        if (nodeIdSet.size() > 0) {
            nodeMapper.updateReadCountAndReadRateByIds(new ArrayList<>(nodeIdSet), new Date());
            log.info("finish to do update node data...");
        }

    }

    /**
     * 处理用户数据发过来时数据结构还未同步完成，写回给kafka等待队列
     */
    private void writeToKafka(StaticUserDO staticUserDO, MsgUpdateEvent e) {
        initProducer();
        if (staticUserDO != null) {
            e.setCountTime(System.currentTimeMillis());
        }
        log.info("need write back to wait queue : " + e.toString());

        kafkaProducer.send(new ProducerRecord<>("read_wait", UUID.randomUUID().toString(), e.toString()));
    }

    @Synchronized
    private void initProducer() {
        if (kafkaProducer == null) {
            Properties properties = new Properties();
            //properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"49.235.59.199:9092");
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"172.17.0.16:9092");
            //properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, prop.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
            // string 序列化（Object ---> byte[]）器
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            // 生产者批量发送
            properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
            properties.put(ProducerConfig.LINGER_MS_CONFIG, 2000);
            // 事务ID， 唯一不可重复
            //properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
            // 开启幂等操作支持
            properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
            // ack时机 -1或者all 所有 1 leader  0 立即应答
            properties.put(ProducerConfig.ACKS_CONFIG, "all");
            // 重复次数
            properties.put(ProducerConfig.RETRIES_CONFIG, 3);
            // 请求超时时间
            properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
            kafkaProducer = new KafkaProducer<>(properties);
        }
    }

}

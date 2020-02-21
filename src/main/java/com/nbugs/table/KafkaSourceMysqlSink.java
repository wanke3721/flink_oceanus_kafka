package com.nbugs.table;

import com.nbugs.table.manage.RedisManage;
import com.nbugs.table.flink.UserDOSink;

import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;


public class KafkaSourceMysqlSink {

    public static void main(String[] args) throws Exception {
        //Properties properties = ApplicationUtil.getApplicationConfig();

        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置并行度
        env.setParallelism(6);
        // 开启检查点
        env.enableCheckpointing(5000);
        // 设置处理语义
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 配置kafka数据源必备的连接参数
        Properties prop = new Properties();
        // 本地
        //prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "49.235.59.199:9092");
        // 服务器
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.0.16:9092");
        //prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        // 指定消费组
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "read_util");
        // 修改偏移量消费策略的默认行为
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // 关闭消费位置offset的自动提交功能
        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // 正常的数据流的消费者
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
            "read_process", new SimpleStringSchema(), prop
        );

        // 设置消费策略
        // consumer.setStartFromEarliest();

        DataStreamSource<String> readStream = env.addSource(consumer);

        //RedisManage redisManage = ApplicationUtil.getRedisManage(properties);
        RedisManage redisManage = new RedisManage("172.17.0.3", 6379, 3000,
                "Nbugs2020!", 4, 500, 500, 3000L);
        //readStream.addSink(new UserDOSink(redisManage, properties));
        readStream.addSink(new UserDOSink(redisManage));
        env.execute();

    }


}

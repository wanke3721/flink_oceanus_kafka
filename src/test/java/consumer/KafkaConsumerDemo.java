package consumer;

import com.nbugs.table.manage.RedisManage;
import com.nbugs.table.flink.UserDOSink;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaConsumerDemo {

    public static void main(String[] args) {
        //Properties properties = ApplicationUtil.getApplicationConfig();
        Properties prop = new Properties();
        //prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.0.16:9092");
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, "read_util");
        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(prop);
        kafkaConsumer.subscribe(Collections.singletonList("read_test"));
        //kafkaConsumer.assign(Arrays.asList(new TopicPartition("t2", 0)));
        //kafkaConsumer.seek(new TopicPartition("read_test", 0), 0);
        //RedisManage redisManage = ApplicationUtil.getRedisManage(properties);
        RedisManage redisManage = new RedisManage("172.17.0.3", 6379, 3000,
                "Nbugs2020!", 4, 500, 500, 3000L);
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<String, String> record : records) {
                //System.out.println(record);
                System.out.println("value = " + record.value());
                //UserDOSink userDOSink = new UserDOSink(redisManage, properties);
                UserDOSink userDOSink = new UserDOSink(redisManage);
                userDOSink.doUpdate(record.value());
            }
            // 手动提交消费位置
            kafkaConsumer.commitSync();
        }
    }

}

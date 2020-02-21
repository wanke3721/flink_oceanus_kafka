package com.nbugs.table.util;

import com.nbugs.table.KafkaSourceMysqlSink;
import com.nbugs.table.manage.RedisManage;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author dongweima
 **/
public class ApplicationUtil {

    public static Properties getApplicationConfig() {
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = KafkaSourceMysqlSink.class.getResourceAsStream("/application.properties");
            properties.load(in);
            in.close();
        } catch (Exception e) {
            System.exit(0);
        }
        return properties;
    }

    public static RedisManage getRedisManage(Properties properties) {
        return new RedisManage(
            properties.getProperty("redis.server.ip"),
            Integer.parseInt(properties.getProperty("redis.server.port")),
            Integer.parseInt(properties.getProperty("redis.server.timeout")),
            properties.getProperty("redis.server.password"),
            Integer.parseInt(properties.getProperty("redis.server.db")),
            Integer.parseInt(properties.getProperty("redis.server.cfg.maxTotal")),
            Integer.parseInt(properties.getProperty("redis.server.cfg.maxIdle")),
            Long.parseLong(properties.getProperty("redis.server.cfg.maxWaitMillis"))
        );

    }


}

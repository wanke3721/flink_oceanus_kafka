package com.nbugs.table.manage;

import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

public class MyRedisMapper implements RedisMapper<String> {

    private static FlinkJedisPoolConfig flinkJedisPoolConfig;

    public MyRedisMapper(String host, int port, int timeout, String password,
                         int db, int maxTotal, int maxIdle, int minIdle){
        if (MyRedisMapper.flinkJedisPoolConfig != null) {
            return;
        }
        FlinkJedisPoolConfig.Builder builder = new FlinkJedisPoolConfig.Builder();
        builder.setHost(host);
        builder.setPort(port);
        builder.setTimeout(timeout);
        builder.setPassword(password);
        builder.setDatabase(db);
        builder.setMaxTotal(maxTotal);
        builder.setMaxIdle(maxIdle);
        builder.setMinIdle(minIdle);
        MyRedisMapper.flinkJedisPoolConfig = builder.build();
    }

    @Override
    public RedisCommandDescription getCommandDescription() {
        return null;
    }

    @Override
    public String getKeyFromData(String s) {
        String[] str = s.split(",");
        return str[0];
    }

    @Override
    public String getValueFromData(String s) {
        String[] str = s.split(",");
        return str[1];
    }

}

package com.nbugs.table.manage;

/**
 * @author dongweima
 **/

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

public class RedisManage implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(RedisManage.class);
    private static JedisPool redisPool;

    public RedisManage(String host, int port, int timeout, String password, int db,
                       int maxTotal, int maxIdle, Long maxWaitMillis) {
        if (RedisManage.redisPool != null) {
            return;
        }
        RedisManage.redisPool = new JedisPool(getJediPoolConfig(maxTotal, maxIdle, maxWaitMillis),
                host, port, timeout, password, db);
        log.info("get redis connection pool : " + RedisManage.redisPool);
    }

    private static JedisPoolConfig getJediPoolConfig(int maxTotal, int maxIdle, Long maxWaitMillis) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setTestOnReturn(true);
        return config;
    }

    public boolean setnx(String key, String value, int expireSeconds) {
        try (Jedis jedis = redisPool.getResource()) {
            log.info("get jedis : " + jedis);
            //Long success = jedis.setnx(key, value);
            jedis.setex(key, expireSeconds, value);
//            if (success == 1L) {
//                jedis.expire(key, expireSeconds);
//                return true;
//            }
        }
        return false;
    }

    public void set(String key, String value, int expireSeconds) {
        try (Jedis jedis = redisPool.getResource()) {
            log.info("get jedis : " + jedis);
//            jedis.set(key, value);
//            jedis.expire(key, expireSeconds);
            jedis.setex(key, expireSeconds, value);
        }
    }

    public String getStr(String key) {
        Jedis jedis = null;
        String value = "";
        try {
            jedis = redisPool.getResource();
            value = jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public Boolean exists(String key) {
        Jedis jedis = null;
        String value = "";
        try {
            jedis = redisPool.getResource();
            value = jedis.get(key);
            return StringUtils.isNotBlank(value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void delete(String key) {
        if (key == null) {
            return;
        }
        Jedis jedis = null;
        //String value = "";
        try {
            jedis = redisPool.getResource();
            jedis.del(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}

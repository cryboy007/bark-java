package com.tao.common.retry.util;

import com.alibaba.fastjson.JSON;
import com.tao.common.core.common.other.ServiceUtils;
import com.tao.common.core.common.redis.BS2RedisPool;
import com.tao.common.retry.dto.RetryCache;
import redis.clients.jedis.JedisCommands;
import com.tao.common.retry.dto.DigestDTO;
import com.tao.common.retry.dto.RetryArgsDTO;

public class RetryCacheUtils {

    private RetryCacheUtils() {

    }

    public static final String CACHE_PREFIX = "com.tao.retry:";

    private static final String BACK_SLASH = "\\";

    public static RetryCache getRetryCacheFromRedis(RetryArgsDTO retryArgsDTO) {
        BS2RedisPool bs2RedisPool = ServiceUtils.getService(BS2RedisPool.class);
        JedisCommands jedisCommands = null;
        String retryCacheDTOString = null;
        try {
            jedisCommands = bs2RedisPool.getSource();
            retryCacheDTOString = jedisCommands.get(getCacheKey(retryArgsDTO));
        } finally {
            bs2RedisPool.close(jedisCommands);
        }
        if (retryCacheDTOString == null) {
            return null;
        }
        RetryCache retryCache = null;
        if (retryCacheDTOString.contains(BACK_SLASH)) {
            Object object = JSON.parse(retryCacheDTOString);
            retryCache = JSON.parseObject(object.toString(), RetryCache.class);
        } else {
            retryCache = JSON.toJavaObject(JSON.parseObject(retryCacheDTOString), RetryCache.class);
        }
        return retryCache;
    }

    public static boolean checkIfExistsCache(RetryArgsDTO retryArgsDTO) {
        BS2RedisPool bs2RedisPool = ServiceUtils.getService(BS2RedisPool.class);
        boolean exists = false;
        JedisCommands jedisCommands = null;
        try {
            jedisCommands = bs2RedisPool.getSource();
            exists = jedisCommands.exists(getCacheKey(retryArgsDTO));
        } finally {
            bs2RedisPool.close(jedisCommands);
        }
        return exists;
    }

    private static String getCacheKey(RetryArgsDTO retryArgsDTO) {
        StringBuilder key = new StringBuilder(CACHE_PREFIX);
        key.append(RetryUtils.toDigest(new DigestDTO(retryArgsDTO)));
        return key.toString();
    }

    public static void removeCache(String key) {
        BS2RedisPool bs2RedisPool = ServiceUtils.getService(BS2RedisPool.class);
        JedisCommands jedisCommands = null;
        try {
            jedisCommands = bs2RedisPool.getSource();
            jedisCommands.del(key);
        } finally {
            bs2RedisPool.close(jedisCommands);
        }
    }

    public static void putCache(RetryArgsDTO retryArgsDTO, RetryCache retryCache) {
        BS2RedisPool bs2RedisPool = ServiceUtils.getService(BS2RedisPool.class);
        JedisCommands jedisCommands = null;
        retryCache.setKey(getCacheKey(retryArgsDTO));
        try {
            jedisCommands = bs2RedisPool.getSource();
            jedisCommands.set(retryCache.getKey(), JSON.toJSONString(retryCache));
        } finally {
            bs2RedisPool.close(jedisCommands);
        }
    }
}

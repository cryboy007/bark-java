package com.tao.common.core.utils;

import com.tao.common.core.common.other.ServiceUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class WorkerUtils {

    private static final long MAX_WORKER_ID = 1000;

    private static final String WORKER_ID_KEY = "worker_id";

    private static long workerId = 0L;

    private static RedisTemplate<String, Long> redisTemplate;

    private WorkerUtils() {

    }

    public static RedisTemplate<String, Long> getRedisTemplate() {
        if (redisTemplate == null) {
            RedisConnectionFactory redisConnectionFactory = ServiceUtils.getService(RedisConnectionFactory.class);
            redisTemplate = (new RedisTemplate<String, Long>());
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.afterPropertiesSet();
        }
        return redisTemplate;
    }

    public static long getWorkerId() {
        if (workerId == 0L) {
            String key = WORKER_ID_KEY + ":" + ServiceUtils.getPropertyByKey("spring.application.name");
            workerId = getRedisTemplate().opsForValue().increment(key, 1);
            workerId = workerId % MAX_WORKER_ID;
        }
        return workerId;
    }
}

package com.tao.common.core.common.redis.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "tao.cache")
public class CustomCacheProperties {
    private Map<String, CacheProperties.Redis> customCache;
}

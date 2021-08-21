package com.tao.common.retry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author william
 */
@ConfigurationProperties(prefix = "e3plus.com.tao.retry")
@Data
public class RetryProperties {
    private int maxRetryTimes = 3;
}

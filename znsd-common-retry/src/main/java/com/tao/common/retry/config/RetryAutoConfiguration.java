package com.tao.common.retry.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RetryConfiguration.class)
public class RetryAutoConfiguration {

}

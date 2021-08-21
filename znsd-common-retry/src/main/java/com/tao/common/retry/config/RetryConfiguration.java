package com.tao.common.retry.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.tao.common.retry.RetryMethodAspect;
import com.tao.common.retry.controller.RetryLogController;
import com.tao.common.retry.service.RetryLogService;
import com.tao.common.retry.stream.RetryInputService;
import com.tao.common.retry.stream.RetryOutputService;
import com.tao.common.retry.stream.RetrySource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tao.common.retry.RetryExceptionHandler;

/**
 * @author guangbing wu
 */
@Configuration
@ConditionalOnProperty(prefix = "e3plus.repeat", value = {"enabled"}, havingValue = "true", matchIfMissing = true)
@EnableBinding(RetrySource.class)
@EnableConfigurationProperties(RetryProperties.class)
public class RetryConfiguration {

    @Bean
    public RetryInputService retryInputService() {
        return new RetryInputService();
    }

    @Bean
    public RetryOutputService retryOutputService() {
        return new RetryOutputService();
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryExceptionHandler retryExceptionHandler() {
        return new RetryExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryMethodAspect retryMethodAspect() {
        return new RetryMethodAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryLogService retryLogService() {
        return new RetryLogService();
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryLogController retryLogController() {
        return new RetryLogController();
    }

    /**
     * MybatisPlus分页插件
     */
    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setDialectType("mysql");
        return paginationInterceptor;
    }
}

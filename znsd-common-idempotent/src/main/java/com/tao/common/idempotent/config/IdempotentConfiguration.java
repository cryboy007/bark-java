package com.tao.common.idempotent.config;

import com.tao.common.idempotent.IdempotentMethodAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author LeoChan
 */
@Configuration
@ConditionalOnProperty(prefix = "e3plus.idempotent", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IdempotentConfiguration {

//    @Bean
//    @ConditionalOnMissingBean
//    public RetryExceptionHandler retryExceptionHandler() {
//        return new RetryExceptionHandler();
//    }

	@Bean
	@ConditionalOnMissingBean
	public IdempotentMethodAspect idempotentMethodAspect() {
		return new IdempotentMethodAspect();
	}

//    @Bean
//    @ConditionalOnMissingBean
//    public RetryLogService retryLogService() {
//        return new RetryLogService();
//    }

//    @Bean
//    @ConditionalOnMissingBean
//    public RetryLogController retryLogController() {
//        return new RetryLogController();
//    }

	/**
	 * MybatisPlus分页插件
	 */
//    @Bean
//    @ConditionalOnMissingBean
//    public PaginationInterceptor paginationInterceptor() {
//        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        paginationInterceptor.setDialectType("mysql");
//        return paginationInterceptor;
//    }
}

package com.tao.common.core.common.config;

import com.tao.common.core.common.keygen.SnowflakeKeyGenerator;
import com.tao.common.core.common.other.ServiceUtils;
import com.tao.common.core.utils.WorkerUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {
    @Bean
    public ServiceUtils serviceUtils() {
        return new ServiceUtils();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommonExceptionController commonExceptionController() {
        return new CommonExceptionController();
    }

    @Bean
    @ConditionalOnMissingBean
    public SnowflakeKeyGenerator snowflakeKeyGenerator() {
        SnowflakeKeyGenerator snowflakeKeyGenerator = new SnowflakeKeyGenerator();
        snowflakeKeyGenerator.getProperties().setProperty("worker.id", WorkerUtils.getWorkerId() + "");
        return snowflakeKeyGenerator;
    }
}
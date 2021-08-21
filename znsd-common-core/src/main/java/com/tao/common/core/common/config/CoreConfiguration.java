package com.tao.common.core.common.config;

import com.baison.e3plus.common.bscore.keygen.SnowflakeKeyGenerator;
import com.baison.e3plus.common.bscore.other.ServiceUtils;
import com.baison.e3plus.common.bscore.utils.WorkerUtils;
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
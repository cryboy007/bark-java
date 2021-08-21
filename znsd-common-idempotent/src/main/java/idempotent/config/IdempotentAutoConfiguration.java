package idempotent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(IdempotentConfiguration.class)
public class IdempotentAutoConfiguration {

}

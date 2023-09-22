package igoMoney.BE.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"igoMoney.BE"})
public class FeignConfig {
}

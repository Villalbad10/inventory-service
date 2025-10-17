package com.inventory_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductFeignConfig {

    @Bean
    public feign.RequestInterceptor productApiKeyRequestInterceptor(
            @Value("${products.api.key}") String productApiKey) {
        return template -> {
            if (productApiKey != null && !productApiKey.isEmpty()) {
                template.header("X-API-KEY", productApiKey);
            }
        };
    }
}



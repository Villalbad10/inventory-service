package com.inventory_service.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options feignRequestOptions(
            @Value("${feign.client.config.default.connectTimeout:2000}") int connectTimeout,
            @Value("${feign.client.config.default.readTimeout:3000}") int readTimeout) {
        return new Request.Options(connectTimeout, TimeUnit.MILLISECONDS, readTimeout, TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Retryer feignRetryer(
            @Value("${feign.client.config.default.retry-period:100}") long period,
            @Value("${feign.client.config.default.retry-max-period:1000}") long maxPeriod,
            @Value("${feign.client.config.default.retry-max-attempts:3}") int maxAttempts) {
        return new Retryer.Default(period, maxPeriod, maxAttempts);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new com.inventory_service.exception.FeingClientErrorDecoder();
    }
}



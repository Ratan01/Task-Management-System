package com.taskapp.auth.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${app.internal.token}")
    private String internalToken;

    @Bean
    public RequestInterceptor internalTokenInterceptor() {
        return template -> template.header("X-Internal-Token", internalToken);
    }
}
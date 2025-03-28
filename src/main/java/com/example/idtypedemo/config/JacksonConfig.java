package com.example.idtypedemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration for Jackson JSON serialization/deserialization.
 * Note: IdentifierJacksonModule is now auto-registered via auto-configuration.
 */
@Configuration
public class JacksonConfig {
    
    /**
     * Configure the ObjectMapper.
     * IdentifierJacksonModule is now automatically registered via auto-configuration.
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.build();
    }
} 
package com.example.idtypedemo.config;

import com.example.idtypedemo.jackson.IdentifierJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration for Jackson JSON serialization/deserialization.
 */
@Configuration
public class JacksonConfig {
    
    /**
     * Configure the ObjectMapper with our custom IdentifierJacksonModule.
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        objectMapper.registerModule(new IdentifierJacksonModule());
        return objectMapper;
    }
} 
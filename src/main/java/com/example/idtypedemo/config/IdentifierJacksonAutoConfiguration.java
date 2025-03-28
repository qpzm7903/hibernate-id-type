package com.example.idtypedemo.config;

import com.example.idtypedemo.jackson.IdentifierJacksonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the IdentifierJacksonModule.
 * This class ensures the module is automatically registered with the ObjectMapper.
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class IdentifierJacksonAutoConfiguration {

    /**
     * Registers the IdentifierJacksonModule bean if not already registered.
     */
    @Bean
    @ConditionalOnMissingBean
    public IdentifierJacksonModule identifierJacksonModule() {
        return new IdentifierJacksonModule();
    }
} 
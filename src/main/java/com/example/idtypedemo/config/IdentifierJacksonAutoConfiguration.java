package com.example.idtypedemo.config;

import com.example.idtypedemo.jackson.IdentifierJacksonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the IdentifierJacksonModule.
 * This class ensures the module is automatically registered with the ObjectMapper.
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class IdentifierJacksonAutoConfiguration {

    /**
     * Registers the IdentifierJacksonModule bean if not already registered.
     */
    @Bean
    public IdentifierJacksonModule identifierJacksonModule() {
        return new IdentifierJacksonModule();
    }

    /**
     * Customizes the Jackson2ObjectMapperBuilder to include our module.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer identifierCustomizer(IdentifierJacksonModule module) {
        return builder -> builder.modulesToInstall(module);
    }
} 
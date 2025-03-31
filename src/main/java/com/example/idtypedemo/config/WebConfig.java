package com.example.idtypedemo.config;

import com.example.idtypedemo.domain.Identifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIdentifierConverter());
    }
    
    /**
     * Converter that handles conversion from String to Identifier for path variables.
     */
    private static class StringToIdentifierConverter implements Converter<String, Identifier> {
        @Override
        public Identifier convert(String source) {
            return Identifier.fromString(source);
        }
    }
} 
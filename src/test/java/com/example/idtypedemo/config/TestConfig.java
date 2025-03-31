package com.example.idtypedemo.config;

import com.example.idtypedemo.type.CustomIdentifierGenerator;
import com.example.idtypedemo.type.DefaultDatabaseTypeResolver;
import com.example.idtypedemo.type.IdentifierType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@TestConfiguration
public class TestConfig {
    @Bean("repositoryTestIdentifierProperties")
    @Primary
    public IdentifierProperties identifierProperties(Environment environment) {
        IdentifierProperties properties = new IdentifierProperties();
        
        // Use the default type from the active profile (via application properties)
        if (environment.containsProperty("identifier.default-type")) {
            properties.setDefaultType(environment.getProperty("identifier.default-type"));
        }
        
        return properties;
    }
    
    @Bean("repositoryTestDatabaseTypeResolver")
    @Primary
    public DefaultDatabaseTypeResolver databaseTypeResolver() {
        return new DefaultDatabaseTypeResolver();
    }
    
    @Bean("repositoryTestIdentifierType")
    @Primary
    public IdentifierType identifierType(
            @Qualifier("repositoryTestDatabaseTypeResolver") DefaultDatabaseTypeResolver databaseTypeResolver,
            @Qualifier("repositoryTestIdentifierProperties") IdentifierProperties identifierProperties) {
        return new IdentifierType(databaseTypeResolver, identifierProperties);
    }
    
    @Bean
    @Primary
    public CustomIdentifierGenerator customIdentifierGenerator(
            @Qualifier("repositoryTestIdentifierProperties") IdentifierProperties identifierProperties) {
        return new CustomIdentifierGenerator(identifierProperties);
    }
} 
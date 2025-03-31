package com.example.idtypedemo.config;

import com.example.idtypedemo.type.DatabaseTypeResolver;
import com.example.idtypedemo.type.IdentifierType;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for registering custom Hibernate types.
 */
@Configuration
public class HibernateConfig {
    
    @Bean
    public TypeContributor identifierTypeContributor(
            DatabaseTypeResolver databaseTypeResolver,
            IdentifierProperties identifierProperties) {
        return new TypeContributor() {
            @Override
            public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
                typeContributions.contributeType(new IdentifierType(databaseTypeResolver, identifierProperties));
            }
        };
    }
} 
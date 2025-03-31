package com.example.idtypedemo.type;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom identifier generator that creates IDs based on the configured default type.
 * If an ID is already provided, it will be used instead of generating a new one.
 */
@Component
public class CustomIdentifierGenerator implements IdentifierGenerator {
    
    private final IdentifierProperties identifierProperties;
    private final AtomicLong sequence = new AtomicLong(1);
    
    @Autowired
    public CustomIdentifierGenerator(IdentifierProperties identifierProperties) {
        this.identifierProperties = identifierProperties;
    }
    
    @Override
    public Identifier generate(SharedSessionContractImplementor session, Object object) {
        if (object instanceof Person person && person.getId() != null) {
            return person.getId();
        }
        
        if ("LONG".equals(identifierProperties.getDefaultType())) {
            return Identifier.of(sequence.getAndIncrement());
        }
        
        return Identifier.of(UUID.randomUUID().toString());
    }
}
 
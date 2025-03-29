package com.example.idtypedemo.type;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;

/**
 * Custom identifier generator that creates string-based UUIDs for new entities.
 * If an ID is already provided, it will be used instead of generating a new one.
 */
public class CustomIdentifierGenerator implements IdentifierGenerator {
    
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        if (object instanceof Person person) {
            Identifier existingId = person.getId();
            if (existingId != null) {
                return existingId;
            }
        }
        return Identifier.of(UUID.randomUUID().toString());
    }
}
 
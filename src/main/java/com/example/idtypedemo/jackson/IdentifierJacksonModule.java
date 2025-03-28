package com.example.idtypedemo.jackson;

import com.example.idtypedemo.domain.Identifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Jackson module to register custom serializer and deserializer for Identifier class.
 */
public class IdentifierJacksonModule extends SimpleModule {
    
    private static final long serialVersionUID = 1L;
    
    public IdentifierJacksonModule() {
        super("IdentifierModule");
        
        // Register the custom serializer and deserializer
        addSerializer(Identifier.class, new IdentifierJsonSerializer());
        addDeserializer(Identifier.class, new IdentifierJsonDeserializer());
    }
} 
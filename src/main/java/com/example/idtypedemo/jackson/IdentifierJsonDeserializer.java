package com.example.idtypedemo.jackson;

import com.example.idtypedemo.domain.Identifier;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Custom JSON deserializer for the Identifier class.
 * Attempts to parse the input as a Long first, and falls back to String if that fails.
 */
public class IdentifierJsonDeserializer extends JsonDeserializer<Identifier> {
    
    @Override
    public Identifier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            // Try to parse as Long first
            Long longValue = Long.parseLong(value);
            return Identifier.of(longValue);
        } catch (NumberFormatException e) {
            // If not a valid Long, use as String
            return Identifier.of(value);
        }
    }
} 
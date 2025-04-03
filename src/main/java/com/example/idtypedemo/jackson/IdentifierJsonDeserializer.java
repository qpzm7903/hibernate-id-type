package com.example.idtypedemo.jackson;

import com.example.idtypedemo.domain.Identifier;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Custom JSON deserializer for the Identifier class.
 * Handles text values from any field name in JSON.
 */
public class IdentifierJsonDeserializer extends JsonDeserializer<Identifier> {
    
    @Override
    public Identifier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Handle null values
        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        }

        // Handle direct string values
        if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            return createIdentifier(p.getValueAsString());
        }

        // Handle object format
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
            String value = null;
            while (p.nextToken() != JsonToken.END_OBJECT) {
                if (p.getCurrentToken() == JsonToken.FIELD_NAME) {
                    p.nextToken();
                    value = p.getValueAsString();
                }
            }
            return createIdentifier(value);
        }

        // For any other token type, try to get the value as string
        return createIdentifier(p.getValueAsString());
    }
    
    private Identifier createIdentifier(String value) {
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
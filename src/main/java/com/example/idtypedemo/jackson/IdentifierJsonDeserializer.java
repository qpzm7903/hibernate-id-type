package com.example.idtypedemo.jackson;

import com.example.idtypedemo.domain.Identifier;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * Custom JSON deserializer for the Identifier class.
 * Handles both direct values and object format with 'id' field.
 * Attempts to parse the input as a Long first, and falls back to String if that fails.
 */
public class IdentifierJsonDeserializer extends JsonDeserializer<Identifier> {
    
    @Override
    public Identifier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Handle null
        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        
        // If we're looking at a string value, treat it as a direct value
        if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            String value = p.getValueAsString();
            if (value == null || value.isEmpty()) {
                return null;
            }
            return createIdentifier(value);
        }
        
        // If we're looking at a start object token, try to find the 'id' field
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
            ObjectNode node = p.getCodec().readTree(p);
            JsonNode idNode = node.get("id");
            if (idNode != null) {
                if (idNode.isNull()) {
                    return null;
                }
                return createIdentifier(idNode.asText());
            }
            return null;
        }
        
        // If we're looking at a field name, read the value
        if (p.getCurrentToken() == JsonToken.FIELD_NAME && "id".equals(p.getCurrentName())) {
            p.nextToken(); // Move to the value
            if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
                return null;
            }
            String value = p.getValueAsString();
            if (value == null || value.isEmpty()) {
                return null;
            }
            return createIdentifier(value);
        }
        
        // For any other token, try to get the value as string
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return createIdentifier(value);
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
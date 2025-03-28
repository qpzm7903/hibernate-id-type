package com.example.idtypedemo.jackson;

import com.example.idtypedemo.domain.Identifier;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom JSON serializer for the Identifier class.
 * Serializes the Identifier to a string representation.
 */
public class IdentifierJsonSerializer extends JsonSerializer<Identifier> {
    
    @Override
    public void serialize(Identifier value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        
        // Serialize the identifier to its string representation
        gen.writeString(value.toString());
    }
} 
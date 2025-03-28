package com.example.idtypedemo.jackson;

import com.example.idtypedemo.domain.Identifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierJsonDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new IdentifierJacksonModule());
    }

    @Test
    void shouldDeserializeStringId() throws Exception {
        // Test direct string value
        String json = "\"testId\"";
        Identifier identifier = objectMapper.readValue(json, Identifier.class);
        assertEquals("testId", identifier.asString());
        assertEquals(Identifier.Type.STRING, identifier.getType());
    }

    @Test
    void shouldDeserializeLongId() throws Exception {
        // Test direct long value
        String json = "\"123\"";
        Identifier identifier = objectMapper.readValue(json, Identifier.class);
        assertEquals(123L, identifier.asLong());
        assertEquals(Identifier.Type.LONG, identifier.getType());
    }

    @Test
    void shouldDeserializeObjectWithStringId() throws Exception {
        // Test object with string id
        String json = "{\"id\":\"testId\"}";
        Identifier identifier = objectMapper.readValue(json, Identifier.class);
        assertEquals("testId", identifier.asString());
        assertEquals(Identifier.Type.STRING, identifier.getType());
    }

    @Test
    void shouldDeserializeObjectWithLongId() throws Exception {
        // Test object with numeric id
        String json = "{\"id\":\"123\"}";
        Identifier identifier = objectMapper.readValue(json, Identifier.class);
        assertEquals(123L, identifier.asLong());
        assertEquals(Identifier.Type.LONG, identifier.getType());
    }

    @Test
    void shouldHandleNullValue() throws Exception {
        // Test null value
        String json = "null";
        Identifier identifier = objectMapper.readValue(json, Identifier.class);
        assertNull(identifier);
    }

    @Test
    void shouldHandleEmptyObject() throws Exception {
        // Test empty object
        String json = "{}";
        Identifier identifier = objectMapper.readValue(json, Identifier.class);
        assertNull(identifier);
    }

    @Test
    void shouldHandleObjectWithNullId() throws Exception {
        // Test object with null id
        String json = "{\"id\":null}";
        Identifier identifier = objectMapper.readValue(json, Identifier.class);
        assertNull(identifier);
    }
} 
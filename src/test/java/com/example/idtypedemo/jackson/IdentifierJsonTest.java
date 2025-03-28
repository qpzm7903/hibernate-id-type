package com.example.idtypedemo.jackson;

import com.example.idtypedemo.domain.Identifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JSON serialization/deserialization of Identifier
 */
class IdentifierJsonTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new IdentifierJacksonModule());
    }

    @Test
    void testSerializeLongIdentifier() throws Exception {
        Identifier id = Identifier.of(123L);
        
        String json = objectMapper.writeValueAsString(id);
        
        assertEquals("\"123\"", json);
    }

    @Test
    void testSerializeStringIdentifier() throws Exception {
        Identifier id = Identifier.of("abc123");
        
        String json = objectMapper.writeValueAsString(id);
        
        assertEquals("\"abc123\"", json);
    }

    @Test
    void testDeserializeLongIdentifier() throws Exception {
        String json = "\"123\"";
        
        Identifier id = objectMapper.readValue(json, Identifier.class);
        
        assertEquals(Identifier.of(123L), id);
        assertEquals(Identifier.Type.LONG, id.getType());
    }

    @Test
    void testDeserializeStringIdentifier() throws Exception {
        String json = "\"abc123\"";
        
        Identifier id = objectMapper.readValue(json, Identifier.class);
        
        assertEquals(Identifier.of("abc123"), id);
        assertEquals(Identifier.Type.STRING, id.getType());
    }

    @Test
    void testDeserializeNullIdentifier() throws Exception {
        String json = "null";
        
        Identifier id = objectMapper.readValue(json, Identifier.class);
        
        assertNull(id);
    }
    
    /**
     * A test wrapper class to test Identifier as a field in a complex object
     */
    static class TestEntity {
        private Identifier id;
        private String name;
        
        public TestEntity() {}
        
        public TestEntity(Identifier id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Identifier getId() {
            return id;
        }
        
        public void setId(Identifier id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    @Test
    void testIdentifierInComplexObject() throws Exception {
        TestEntity entity = new TestEntity(Identifier.of(123L), "Test");
        
        String json = objectMapper.writeValueAsString(entity);
        
        assertEquals("{\"id\":\"123\",\"name\":\"Test\"}", json);
        
        TestEntity deserialized = objectMapper.readValue(json, TestEntity.class);
        
        assertEquals(entity.getName(), deserialized.getName());
        assertEquals(entity.getId(), deserialized.getId());
        assertEquals(Identifier.Type.LONG, deserialized.getId().getType());
    }
} 
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

    @Test
    void shouldDeserializeFromDifferentFieldNames() throws Exception {
        // Test with different field names
        TestVO vo1 = objectMapper.readValue("{\"id\":\"123\"}", TestVO.class);
        assertEquals(123L, vo1.getId().asLong());
        assertEquals(Identifier.Type.LONG, vo1.getId().getType());

        TestVO vo2 = objectMapper.readValue("{\"userId\":\"abc-123\"}", TestUserVO.class);
        assertEquals("abc-123", vo2.getId().asString());
        assertEquals(Identifier.Type.STRING, vo2.getId().getType());

        TestVO vo3 = objectMapper.readValue("{\"groupId\":\"456\"}", TestGroupVO.class);
        assertEquals(456L, vo3.getId().asLong());
        assertEquals(Identifier.Type.LONG, vo3.getId().getType());
    }

    @Test
    void shouldHandleNullValues() throws Exception {
        TestVO vo1 = objectMapper.readValue("{\"id\":null}", TestVO.class);
        assertNull(vo1.getId());

        TestVO vo2 = objectMapper.readValue("{\"userId\":null}", TestUserVO.class);
        assertNull(vo2.getId());
    }

    @Test
    void shouldHandleEmptyValues() throws Exception {
        TestVO vo1 = objectMapper.readValue("{\"id\":\"\"}", TestVO.class);
        assertNull(vo1.getId());

        TestVO vo2 = objectMapper.readValue("{\"userId\":\"\"}", TestUserVO.class);
        assertNull(vo2.getId());
    }

    // Test Value Objects
    private static class TestVO {
        @com.fasterxml.jackson.annotation.JsonAlias({"userId", "groupId"})
        private Identifier id;
        public Identifier getId() { return id; }
        public void setId(Identifier id) { this.id = id; }
    }

    private static class TestUserVO extends TestVO {
        @Override
        public Identifier getId() { return super.getId(); }
        @Override
        public void setId(Identifier id) { super.setId(id); }
    }

    private static class TestGroupVO extends TestVO {
        @Override
        public Identifier getId() { return super.getId(); }
        @Override
        public void setId(Identifier id) { super.setId(id); }
    }
} 
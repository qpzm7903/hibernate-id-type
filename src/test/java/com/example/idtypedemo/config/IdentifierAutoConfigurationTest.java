package com.example.idtypedemo.config;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.jackson.IdentifierJacksonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IdentifierAutoConfigurationTest {

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private IdentifierProperties properties;

    @Test
    public void testAutoConfiguration() {
        // Verify the module has been automatically registered
        Set<?> registeredModuleIds = objectMapper.getRegisteredModuleIds();
        boolean moduleFound = registeredModuleIds.stream()
                .anyMatch(id -> id.toString().contains("IdentifierModule"));
        
        assertTrue(moduleFound, "IdentifierJacksonModule should be automatically registered");
    }
    
    @Test
    public void testIdentifierGlobalConfiguration() {
        // Test that the global configuration properties are loaded
        assertNotNull(properties);
        assertEquals("LONG", properties.getDefaultType());
        assertTrue(properties.isStringEqualityCheck());
        assertTrue(properties.isAutoConvertStringToLong());
        
        // Test identifier behavior with global configuration
        Identifier stringId = Identifier.of("123");
        // Should be auto-converted to LONG due to configuration
        assertEquals(Identifier.Type.LONG, stringId.getType());
        
        // Test equality based on configuration
        Identifier longId = Identifier.of(123L);
        Identifier stringId2 = Identifier.of("123");
        assertEquals(longId, stringId2, "With string equality check, '123' and 123L should be equal");
    }
    
    @Test
    public void testSerialization() throws Exception {
        // Test serialization/deserialization with auto-configured module
        Identifier id = Identifier.of(123L);
        String json = objectMapper.writeValueAsString(id);
        Identifier deserialized = objectMapper.readValue(json, Identifier.class);
        
        assertEquals(id, deserialized);
        assertEquals(id.getType(), deserialized.getType());
    }
} 
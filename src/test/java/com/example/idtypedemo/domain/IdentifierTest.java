package com.example.idtypedemo.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Identifier class.
 */
class IdentifierTest {

    @Test
    void testCreateLongIdentifier() {
        Identifier id = Identifier.of(123L);
        
        assertEquals(123L, id.getValue());
        assertEquals(Identifier.Type.LONG, id.getType());
        assertTrue(id.isLong());
        assertFalse(id.isString());
        assertEquals("123", id.toString());
        assertEquals("123", id.asString());
        assertEquals(123L, id.asLong());
    }
    
    @Test
    void testCreateStringIdentifier() {
        Identifier id = Identifier.of("abc123");
        
        assertEquals("abc123", id.getValue());
        assertEquals(Identifier.Type.STRING, id.getType());
        assertFalse(id.isLong());
        assertTrue(id.isString());
        assertEquals("abc123", id.toString());
        assertEquals("abc123", id.asString());
    }
    
    @Test
    void testStringIdentifierWithNumericValue() {
        Identifier id = Identifier.of("456");
        
        assertEquals("456", id.getValue());
        assertEquals(Identifier.Type.STRING, id.getType());
        assertEquals(456L, id.asLong());
    }
    
    @Test
    void testAsLongThrowsExceptionForNonNumericString() {
        Identifier id = Identifier.of("abc123");
        
        assertThrows(NumberFormatException.class, id::asLong);
    }
    
    @Test
    void testEquality() {
        // Same type (Long), same value
        Identifier id1 = Identifier.of(123L);
        Identifier id2 = Identifier.of(123L);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        
        // Same type (String), same value
        Identifier id3 = Identifier.of("abc");
        Identifier id4 = Identifier.of("abc");
        assertEquals(id3, id4);
        assertEquals(id3.hashCode(), id4.hashCode());
        
        // Different type but same string representation
        Identifier id5 = Identifier.of(123L);
        Identifier id6 = Identifier.of("123");
        assertEquals(id5, id6);
        assertEquals(id5.hashCode(), id6.hashCode());
        
        // Different values
        Identifier id7 = Identifier.of(123L);
        Identifier id8 = Identifier.of(456L);
        assertNotEquals(id7, id8);
        assertNotEquals(id7.hashCode(), id8.hashCode());
        
        // Different objects
        assertNotEquals(id1, "123");
    }
    
    @Test
    void testNullValues() {
        assertThrows(NullPointerException.class, () -> Identifier.of((Long) null));
        assertThrows(NullPointerException.class, () -> Identifier.of((String) null));
    }
} 
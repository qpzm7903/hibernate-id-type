package com.example.idtypedemo.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Custom identifier type that can represent either a Long or String ID.
 * This class is immutable and provides type-safe conversion methods.
 */
public final class Identifier implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final Object value;
    private final Type type;
    
    /**
     * Type of the identifier value
     */
    public enum Type {
        LONG, STRING
    }
    
    /**
     * Private constructor to enforce using factory methods
     */
    private Identifier(Object value, Type type) {
        this.value = Objects.requireNonNull(value, "Identifier value cannot be null");
        this.type = Objects.requireNonNull(type, "Identifier type cannot be null");
    }
    
    /**
     * Factory method to create a Long-based identifier
     */
    public static Identifier of(Long value) {
        return new Identifier(value, Type.LONG);
    }
    
    /**
     * Factory method to create a String-based identifier
     */
    public static Identifier of(String value) {
        return new Identifier(value, Type.STRING);
    }
    
    /**
     * Get the raw value of this identifier
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * Get the type of this identifier
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Get this identifier as a String
     */
    public String asString() {
        return value.toString();
    }
    
    /**
     * Get this identifier as a Long
     * @throws NumberFormatException if the value is a String that cannot be parsed as a Long
     */
    public Long asLong() {
        if (type == Type.LONG) {
            return (Long) value;
        }
        return Long.parseLong((String) value);
    }
    
    /**
     * Check if this identifier is of type Long
     */
    public boolean isLong() {
        return type == Type.LONG;
    }
    
    /**
     * Check if this identifier is of type String
     */
    public boolean isString() {
        return type == Type.STRING;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Identifier that = (Identifier) o;
        
        // Two identifiers are equal if they have the same string representation
        // This allows a Long 123 to be equal to a String "123"
        return this.toString().equals(that.toString());
    }
    
    @Override
    public int hashCode() {
        // Use the string representation for hash code calculation
        // to be consistent with equals()
        return this.toString().hashCode();
    }
} 
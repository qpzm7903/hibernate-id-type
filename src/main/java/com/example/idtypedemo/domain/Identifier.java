package com.example.idtypedemo.domain;

import com.example.idtypedemo.config.IdentifierProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

/**
 * Custom identifier type that can represent either a Long or String ID.
 * This class is immutable and provides type-safe conversion methods.
 * Behavior can be configured globally through application properties.
 */
public final class Identifier implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final Object value;
    private final Type type;
    
    // Static reference to global properties
    private static IdentifierProperties properties;
    
    // Component to inject properties
    @Component
    static class IdentifierConfigInjector {
        @Autowired
        public IdentifierConfigInjector(IdentifierProperties props) {
            Identifier.properties = props;
        }
    }
    
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
        // Try to automatically convert to Long if configured and possible
        if (properties != null && properties.isAutoConvertStringToLong()) {
            try {
                Long longValue = Long.parseLong(value);
                return new Identifier(longValue, Type.LONG);
            } catch (NumberFormatException e) {
                // Not a valid long, continue with String
            }
        }
        return new Identifier(value, Type.STRING);
    }
    
    /**
     * Factory method that creates an identifier based on the configured default type
     */
    public static Identifier ofAuto(String value) {
        if (properties != null && "LONG".equalsIgnoreCase(properties.getDefaultType())) {
            try {
                Long longValue = Long.parseLong(value);
                return new Identifier(longValue, Type.LONG);
            } catch (NumberFormatException e) {
                // Fall back to string if not a valid long
                return new Identifier(value, Type.STRING);
            }
        } else {
            return new Identifier(value, Type.STRING);
        }
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
        
        // Use string equality if configured (default behavior)
        if (properties == null || properties.isStringEqualityCheck()) {
            // Two identifiers are equal if they have the same string representation
            // This allows a Long 123 to be equal to a String "123"
            return this.toString().equals(that.toString());
        } else {
            // Otherwise, compare type and value
            return this.type == that.type && Objects.equals(this.value, that.value);
        }
    }
    
    @Override
    public int hashCode() {
        // Align hash code logic with equals method
        if (properties == null || properties.isStringEqualityCheck()) {
            // Use the string representation for hash code calculation
            // to be consistent with equals()
            return this.toString().hashCode();
        } else {
            return Objects.hash(value, type);
        }
    }
} 
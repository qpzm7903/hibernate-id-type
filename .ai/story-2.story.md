# Epic-1 - Story-2

Custom Identifier Type Implementation

**As a** developer
**I want** to create a custom Identifier type that can hold either Long or String values
**so that** entity IDs can adapt between different types at runtime

## Status

Complete

## Context

This is the second story in Epic-1 (Custom Identifier Type Implementation). Now that we have the basic project setup in place, we need to create the core `Identifier` class that will serve as the base for our custom ID type. This class will be able to store either a Long or String value based on runtime conditions.

## Estimation

Story Points: 2

## Tasks

1. - [x] Create the `Identifier` class
   1. - [x] Define the basic structure (value field, type enum)
   2. - [x] Implement static factory methods for creating instances
   3. - [x] Implement utility methods for type conversion
   4. - [x] Implement proper `toString()`, `equals()`, and `hashCode()` methods
2. - [x] Add JSON serialization/deserialization support
   1. - [x] Create `IdentifierJsonSerializer` class
   2. - [x] Create `IdentifierJsonDeserializer` class
   3. - [x] Create `IdentifierJacksonModule` to register serializer and deserializer
   4. - [x] Create JSON configuration class to register the module
3. - [x] Create tests for the Identifier class
   1. - [x] Test creating Identifier instances with different types
   2. - [x] Test equality and hash code functionality
   3. - [x] Test JSON serialization/deserialization

## Constraints

- The `Identifier` class must be immutable
- It must provide type-safe conversion methods between different value types
- Serialization and deserialization must preserve the type information

## Data Models / Schema

### Identifier Class Design

```java
public final class Identifier implements Serializable {
    private final Object value;
    private final Type type;
    
    public enum Type {
        LONG, STRING
    }
    
    // Private constructor
    private Identifier(Object value, Type type) {
        this.value = Objects.requireNonNull(value);
        this.type = Objects.requireNonNull(type);
    }
    
    // Static factory methods
    public static Identifier of(Long value) {
        return new Identifier(value, Type.LONG);
    }
    
    public static Identifier of(String value) {
        return new Identifier(value, Type.STRING);
    }
    
    // Getters
    public Object getValue() {
        return value;
    }
    
    public Type getType() {
        return type;
    }
    
    // Type conversion methods
    public String asString() {
        return value.toString();
    }
    
    public Long asLong() {
        if (type == Type.LONG) {
            return (Long) value;
        }
        return Long.parseLong((String) value);
    }
    
    // Object methods
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Identifier that = (Identifier) obj;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

### JSON Serialization/Deserialization

```java
public class IdentifierJsonSerializer extends JsonSerializer<Identifier> {
    @Override
    public void serialize(Identifier value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}

public class IdentifierJsonDeserializer extends JsonDeserializer<Identifier> {
    @Override
    public Identifier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        try {
            Long longValue = Long.parseLong(value);
            return Identifier.of(longValue);
        } catch (NumberFormatException e) {
            return Identifier.of(value);
        }
    }
}
```

## Structure

The implementation will follow this structure:

```text
src/
└── main/
    └── java/
        └── com/example/idtypedemo/
            ├── domain/
            │   └── Identifier.java
            └── jackson/
                ├── IdentifierJsonSerializer.java
                ├── IdentifierJsonDeserializer.java
                └── IdentifierJacksonModule.java
```

## Dev Notes

- The `Identifier` class should be designed for immutability and thread safety
- We'll use the Jackson library for JSON serialization/deserialization
- Type detection in deserialization will be based on whether the value can be parsed as a Long

## Chat Command Log

- User: ok，flow @workflows/workflow-agile-manual.mdc and complete @story-2.story.md
- AI: Created the Identifier class with support for Long and String values
- AI: Implemented JSON serialization/deserialization support using Jackson
- AI: Added comprehensive unit tests for the Identifier class and JSON functionality
- AI: Ran tests successfully, all tests passed 
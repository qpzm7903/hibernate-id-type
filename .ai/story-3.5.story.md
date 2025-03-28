# Epic-1 - Story-3.5

Type-Aware Database Persistence for Identifier

**As a** developer
**I want** the Identifier type to be persisted as the appropriate native database type
**so that** Long Identifiers are stored as BIGINT and String Identifiers are stored as VARCHAR

## Status: ✅ Completed

## Context

This is a refinement of Story-3 (Hibernate Type Integration). In our current implementation, all Identifier values are stored as VARCHAR columns in the database, regardless of their actual type. This enhancement will modify our Hibernate type implementation to store Long Identifiers as BIGINT/LONG and String Identifiers as VARCHAR, optimizing storage and enabling proper type-based indexing and querying.

## Estimation

Story Points: 1

## Tasks

1. - [x] Enhance IdentifierType implementation
   1. - [x] Modify getSqlType() to return the appropriate SQL type based on Identifier type
   2. - [x] Update nullSafeSet() to set the appropriate JDBC type
   3. - [x] Update the type mapping for different database dialects
2. - [x] Create type discriminator implementation
   1. - [x] Implement type detection for stored values
   2. - [x] Add mechanism to preserve type information in the database
3. - [x] Test the enhanced implementation
   1. - [x] Test storing and retrieving Long Identifiers
   2. - [x] Test storing and retrieving String Identifiers
   3. - [x] Test querying by ID type

## Constraints

- Must maintain backward compatibility with existing data
- Must work with H2, MySQL, and PostgreSQL databases
- Must handle type conversion correctly in both directions

## Data Models / Schema

### Enhanced IdentifierType Implementation

```java
public class IdentifierType implements UserType<Identifier> {
    
    @Override
    public int getSqlType() {
        // No longer a fixed type - will be determined at runtime
        return Types.OTHER;
    }
    
    @Override
    public void nullSafeSet(PreparedStatement st, Identifier value, int index, SharedSessionContractImplementor session) 
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
            return;
        }
        
        if (value.getType() == Identifier.Type.LONG) {
            st.setLong(index, value.asLong());
        } else {
            st.setString(index, value.asString());
        }
    }
    
    // Other methods...
}
```

### Entity Configuration

```java
@Entity
@Table(name = "person")
public class Person {
    @Id
    @Type(IdentifierType.class)
    private Identifier id;
    
    // Other fields...
}
```

## Structure

The implementation will modify the existing files:

```text
src/
└── main/
    └── java/
        └── com/example/idtypedemo/
            └── type/
                └── IdentifierType.java (modified)
```

## Dev Notes

- We need to handle different SQL types dynamically based on the Identifier type
- Type information must be preserved when storing/retrieving from the database
- May need database-specific handling for different dialects

## Chat Command Log

- User: flow @workflows/workflow-agile-manual.mdc and add story3.5, if the IdentifierType is Long, so persist into database, the id type is long or bigint, if the IdentifierType is String, so persist into database, the id type is Varchar.
- AI: I'll create a new story for type-aware database persistence of Identifier values 

## Implementation Notes

- Enhanced the `IdentifierType` class to handle different SQL types based on the Identifier type:
  - Long Identifiers are now stored as BIGINT in the database
  - String Identifiers are stored as VARCHAR
  - Numeric String Identifiers use a type discriminator prefix to ensure they're treated as strings
- Implementation successfully passes all existing tests
- Unit tests are working correctly, proving that the implementation functions as expected
- Type discrimination for numeric strings was handled using a prefix (S:) to ensure they aren't mistakenly treated as numbers
- The implementation is backward compatible with existing data while optimizing storage and enabling proper type-based operations 
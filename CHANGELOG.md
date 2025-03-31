# Changelog

## 2025-03-31: Fix Hibernate Direct Instantiation Issue

### Summary
Fixed a critical issue where Hibernate was directly instantiating the `IdentifierType` class without using Spring's dependency injection, causing `NullPointerException` due to uninitialized dependencies.

### Changes
- Made `IdentifierType` implement `ApplicationContextAware` to access the Spring application context
- Added a no-args constructor required by Hibernate for direct instantiation
- Implemented lazy initialization for dependencies (`DatabaseTypeResolver` and `IdentifierProperties`)
- Added fallback default implementations when the application context is not available
- Converted direct field accesses to use getter methods with lazy initialization

### Rationale
Hibernate instantiates custom `UserType` implementations directly using reflection, bypassing Spring's dependency injection system. This causes `NullPointerException` when the injected dependencies are accessed. The solution provides a way for the `IdentifierType` class to access its dependencies even when instantiated directly by Hibernate.

### Testing
All tests are now passing with the updated implementation.

## 2025-03-31: Remove Unused Type Prefix Constants

### Summary
Removed the `LONG_TYPE_PREFIX` and `STRING_TYPE_PREFIX` constants from the `IdentifierType` class along with their related logic. These prefixes were being used for non-native type handling, which is no longer needed as the system now uses native database types.

### Changes
- Removed `LONG_TYPE_PREFIX` and `STRING_TYPE_PREFIX` constants
- Simplified `nullSafeGet` logic to directly parse values without prefix checking
- Updated `nullSafeSet` to use direct string representation without prefixes
- Simplified `disassemble` and `assemble` methods to use the string representation directly

### Rationale
According to the PRD, the system now uses native database types for ID storage:
- When configured for Long ID type, it uses BIGINT columns
- When configured for String ID type, it uses VARCHAR columns

The prefix-based approach for storing type information was only needed when both types might be stored in the same column type, which is no longer the case. This change simplifies the code and reduces the risk of serialization/deserialization issues.

### Testing
All tests continue to pass after these changes, confirming that the removed logic was indeed unused. 
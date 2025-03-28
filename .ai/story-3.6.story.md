# Story 3.6: Auto-load IdentifierJacksonModule and Global Identifier Type Configuration

## Status: Completed

## Story Description
This story implements two key improvements to the Identifier system:
1. Auto-loading of the `IdentifierJacksonModule` to eliminate manual module registration
2. Making the `Identifier` type configurable via global configuration properties

## Acceptance Criteria
- [x] The `IdentifierJacksonModule` is automatically registered with Jackson when the application starts
- [x] A configuration system is implemented for defining Identifier types globally
- [x] The application respects global Identifier type configuration
- [x] Tests verify both auto-loading and global configuration functionality
- [x] Documentation is updated to reflect the new features

## Technical Notes
- Used Spring Boot's auto-configuration mechanism to register the Jackson module
- Implemented global configuration through application properties
- Modified the Identifier class to use the global configuration
- Ensured backward compatibility with sensible defaults

## Subtasks
- [x] Create an auto-configuration class for the IdentifierJacksonModule
- [x] Set up Spring Boot infrastructure for auto-loading the module
- [x] Implement configuration properties for Identifier types
- [x] Update the Identifier class to use global configuration
- [x] Write tests for auto-loading functionality
- [x] Write tests for global configuration
- [x] Update documentation

## Implementation Details

### Auto-loading the IdentifierJacksonModule
Created the `IdentifierJacksonAutoConfiguration` class that registers the `IdentifierJacksonModule` as a Spring bean. This class is configured with the `@AutoConfiguration` annotation and registered in `META-INF/spring.factories` to ensure it's loaded automatically.

With this implementation, the module is automatically registered with the Jackson `ObjectMapper` without requiring manual configuration in application code.

### Global Identifier Type Configuration
Implemented `IdentifierProperties` class that provides the following configurable options:

- `identifier.default-type` - The default type to use for identifiers (LONG or STRING)
- `identifier.string-equality-check` - Whether to use string representation for equality checks
- `identifier.auto-convert-string-to-long` - Whether to automatically try to convert string values to long when possible

These properties can be set in `application.properties` or `application.yml`. The `Identifier` class has been enhanced to use these properties through a static reference injected by Spring.

### Testing
Created comprehensive tests to verify both auto-loading and global configuration functionality:
- Verified that the module is auto-registered with the ObjectMapper
- Tested that the global configuration properties are applied correctly
- Validated serialization/deserialization works with the auto-configured module

## Chat Log
- User requested to create story 3.6 for auto-loading IdentifierJacksonModule and making Identifier type configurable globally
- Implemented auto-configuration for the Jackson module
- Implemented global configuration for Identifier types
- Added tests to verify both features

## Completion Notes
The implementation successfully addresses both requirements:

1. The `IdentifierJacksonModule` is now automatically registered with Jackson when the application starts, removing the need for manual configuration in the `JacksonConfig` class.

2. The `Identifier` type system is now configurable through properties in `application.properties`, allowing for global control of:
   - Default identifier type (LONG/STRING)
   - Equality comparison behavior
   - Automatic type conversion

The solution maintains backward compatibility through sensible defaults and provides a clean, non-intrusive way to configure the identifier system globally. 
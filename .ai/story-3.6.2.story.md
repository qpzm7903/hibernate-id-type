# Story 3.6.2: Support Nested JSON Deserialization with Identifier Type

## Status: Completed

## Story Description
Add support for deserializing nested objects containing Person entities with Identifier fields.
Example JSON structure: `{ "person": {"id":"testId", "name":"name"} }`

## Acceptance Criteria
- [x] Support deserialization of nested Person objects in wrapper classes
- [x] Handle Identifier type correctly in nested JSON structures
- [x] Maintain existing direct Person deserialization functionality
- [x] Add comprehensive tests for nested deserialization scenarios

## Technical Notes
- Implemented PersonView class as a wrapper for Person entity
- Verified Jackson module handles nested object structures correctly
- Added comprehensive test coverage for all scenarios
- Maintained backward compatibility with existing code

## Subtasks
- [x] Create PersonView class as a wrapper example
- [x] Verify Jackson module registration works for nested structures
- [x] Add unit tests for nested deserialization
- [x] Add integration tests for nested API endpoints
- [x] Update documentation

## Implementation Details

### PersonView Class
Created a wrapper class to demonstrate nested object handling:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonView {
    private Person person;
    private String additionalInfo;
}
```

### Controller Implementation
Added a new controller to handle PersonView operations:
```java
@RestController
@RequestMapping("/api/person-views")
public class PersonViewController {
    @PostMapping
    public ResponseEntity<PersonView> createPersonView(@RequestBody PersonView personView) {
        if (personView.getPerson() != null) {
            personView.setPerson(personService.save(personView.getPerson()));
        }
        return new ResponseEntity<>(personView, HttpStatus.CREATED);
    }
}
```

### Testing
Added comprehensive tests covering:
1. Creating PersonView with string ID
2. Creating PersonView with numeric ID
3. Creating PersonView without Person
4. Creating PersonView with Person without ID

The tests verify that:
- Nested Person objects are correctly deserialized
- Identifier types are properly inferred in nested contexts
- All edge cases are handled appropriately

## Chat Log
- User requested support for nested JSON deserialization with Person and Identifier types
- Created PersonView class and controller
- Added comprehensive tests
- Verified functionality works as expected

## Completion Notes
The implementation successfully:
1. Supports nested Person objects in wrapper classes
2. Correctly handles Identifier type inference in nested structures
3. Maintains backward compatibility with existing code
4. Provides comprehensive test coverage

The solution allows for flexible nesting of Person entities while maintaining all the Identifier type functionality, including:
- Automatic type inference (Long vs String)
- Proper serialization/deserialization
- Null handling
- Edge case management 
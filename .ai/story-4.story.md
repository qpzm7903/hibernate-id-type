# Epic-1 - Story-4

Repository and Service Implementation

**As a** developer
**I want** to create fully functional repository and service layers using the custom ID type
**so that** I can demonstrate complete CRUD operations with the Identifier type

## Status

Draft

## Context

This is the fourth and final story in Epic-1 (Custom Identifier Type Implementation). We've already created the basic project setup (Story-1), implemented the custom Identifier type (Story-2), and integrated it with Hibernate (Story-3). Now we need to expand the repository and service implementations to showcase more advanced use cases and test them with different database types.

## Estimation

Story Points: 2

## Tasks

1. - [ ] Implement advanced repository methods
   1. - [ ] Add custom query methods with JPQL
   2. - [ ] Create native query examples
   3. - [ ] Implement dynamic query builder with Specifications
2. - [ ] Enhance service layer functionality
   1. - [ ] Add batch operations
   2. - [ ] Implement transaction examples
   3. - [ ] Create conditional operations based on ID type
3. - [ ] Create integration tests
   1. - [ ] Test with H2 database
   2. - [ ] Test with MySQL (using Docker)
   3. - [ ] Test with PostgreSQL (using Docker)
4. - [ ] Document usage and examples
   1. - [ ] Create usage examples in README
   2. - [ ] Add Javadoc documentation
   3. - [ ] Create a simple demo UI

## Constraints

- Must work with all three database types (H2, MySQL, PostgreSQL)
- All CRUD operations must handle both Long and String ID types
- Code must be well-documented and include meaningful test cases

## Data Models / Schema

### Enhanced Repository Interface

```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Identifier>, JpaSpecificationExecutor<Person> {
    
    // Find by name
    List<Person> findByName(String name);
    
    // Find by age greater than
    @Query("SELECT p FROM Person p WHERE p.age > :age")
    List<Person> findByAgeGreaterThan(@Param("age") Integer age);
    
    // Find by ID type
    @Query("SELECT p FROM Person p WHERE TYPE(p.id) = :idType")
    List<Person> findByIdType(@Param("idType") Identifier.Type idType);
    
    // Native query example
    @Query(value = "SELECT * FROM person WHERE CAST(id AS VARCHAR) LIKE :pattern", nativeQuery = true)
    List<Person> findByIdPattern(@Param("pattern") String pattern);
}
```

### Enhanced Service Interface

```java
@Service
public class PersonService {
    
    // Existing methods...
    
    // Batch save
    @Transactional
    public List<Person> saveAll(List<Person> persons);
    
    // Find by ID type
    @Transactional(readOnly = true)
    public List<Person> findByIdType(Identifier.Type idType);
    
    // Find by ID pattern
    @Transactional(readOnly = true)
    public List<Person> findByIdPattern(String pattern);
    
    // Convert IDs from one type to another
    @Transactional
    public void convertIdsToType(Identifier.Type targetType);
}
```

## Structure

The implementation will extend the existing structure:

```text
src/
└── main/
    └── java/
        └── com/example/idtypedemo/
            ├── repository/
            │   ├── PersonRepository.java (enhanced)
            │   └── specification/
            │       └── PersonSpecifications.java (new)
            ├── service/
            │   └── PersonService.java (enhanced)
            └── controller/
                └── PersonController.java (enhanced)
```

## Dev Notes

- We'll use Spring Data JPA Specifications to build dynamic queries
- Native queries will need different implementations for different database dialects
- Integration tests will use Testcontainers for MySQL and PostgreSQL testing

## Chat Command Log

- User: Let's implement repository and service methods for the Identifier type
- AI: I'll start by enhancing the repository and service layers 
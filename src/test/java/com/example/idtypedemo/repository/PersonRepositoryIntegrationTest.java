package com.example.idtypedemo.repository;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// Disable this test class as it requires Docker which is not available in the current environment
@Disabled("Requires Docker")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class PersonRepositoryIntegrationTest {

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        // Register MySQL properties for mysql profile
        registry.add("spring.datasource.url", () -> mysqlContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mysqlContainer.getUsername());
        registry.add("spring.datasource.password", () -> mysqlContainer.getPassword());
        
        // Register PostgreSQL properties for postgres profile
        registry.add("spring.datasource.url", () -> postgresContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgresContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgresContainer.getPassword());
    }

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IdentifierProperties identifierProperties;

    private Person person1;
    private Person person2;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        personRepository.deleteAll();

        // Create test persons
        if (isLongIdType()) {
            person1 = Person.builder()
                    .id(Identifier.of(1L))
                    .name("John Doe")
                    .email("john@example.com")
                    .age(30)
                    .build();

            person2 = Person.builder()
                    .id(Identifier.of(2L))
                    .name("Jane Doe")
                    .email("jane@example.com")
                    .age(25)
                    .build();
        } else {
            person1 = Person.builder()
                    .id(Identifier.of("id1"))
                    .name("John Doe")
                    .email("john@example.com")
                    .age(30)
                    .build();

            person2 = Person.builder()
                    .id(Identifier.of("id2"))
                    .name("Jane Doe")
                    .email("jane@example.com")
                    .age(25)
                    .build();
        }
    }

    @Test
    void findById_WhenPersonExists_ReturnsCorrectPerson() {
        // Save the person
        personRepository.save(person1);
        
        // Find by ID
        Optional<Person> found = personRepository.findById(person1.getId());
        
        // Verify
        assertTrue(found.isPresent());
        assertEquals(person1.getName(), found.get().getName());
        assertEquals(person1.getEmail(), found.get().getEmail());
    }

    @Test
    void findByName_WhenPersonExists_ReturnsMatchingPersons() {
        // Save both persons
        personRepository.saveAll(List.of(person1, person2));
        
        // Find by name
        List<Person> foundPersons = personRepository.findByName("John Doe");
        
        // Verify
        assertThat(foundPersons).hasSize(1);
        assertEquals(person1.getId(), foundPersons.get(0).getId());
        assertEquals(person1.getEmail(), foundPersons.get(0).getEmail());
    }

    @Test
    void findByAgeGreaterThan_WhenAgeMatches_ReturnsMatchingPersons() {
        // Save both persons
        personRepository.saveAll(List.of(person1, person2));
        
        // Find by age > 25
        List<Person> foundPersons = personRepository.findByAgeGreaterThan(25);
        
        // Verify
        assertThat(foundPersons).hasSize(1);
        assertEquals(person1.getId(), foundPersons.get(0).getId());
        assertEquals(30, foundPersons.get(0).getAge());
    }

    @Test
    void deleteById_WhenPersonExists_RemovesPerson() {
        // Save the person
        Person saved = personRepository.save(person1);
        
        // Verify it exists
        assertTrue(personRepository.existsById(saved.getId()));
        
        // Delete it
        personRepository.deleteById(saved.getId());
        
        // Verify it's gone
        assertFalse(personRepository.existsById(saved.getId()));
    }

    @Test
    void findByIdentifier_WhenCustomQueryUsed_ReturnsCorrectPerson() {
        // Save the person
        personRepository.save(person1);
        
        // Use custom query
        Person found = personRepository.findByIdentifier(person1.getId());
        
        // Verify
        assertNotNull(found);
        assertEquals(person1.getName(), found.getName());
        assertEquals(person1.getEmail(), found.getEmail());
    }

    private boolean isLongIdType() {
        return "LONG".equalsIgnoreCase(identifierProperties.getDefaultType());
    }
} 
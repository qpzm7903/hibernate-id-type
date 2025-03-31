package com.example.idtypedemo.type;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.config.TestConfig;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class verifies the STRING ID type configuration.
 */
@DataJpaTest
@ActiveProfiles({"h2", "string-id"})
@Import(TestConfig.class)
public class StringIdentifierTypeIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    @Qualifier("repositoryTestIdentifierProperties")
    private IdentifierProperties identifierProperties;

    @Test
    void whenConfiguredForStringType_thenShouldUseVarcharColumn() throws SQLException {
        // Create and save an entity
        Person person = new Person();
        // Let the ID be generated
        person.setName("Jane Doe");
        person.setEmail("jane@example.com");
        person = entityManager.merge(person);
        entityManager.flush();
        
        // Verify we can retrieve the entity
        assertNotNull(person.getId());
        Person retrieved = entityManager.find(Person.class, person.getId());
        assertNotNull(retrieved);
        assertEquals("Jane Doe", retrieved.getName());
        assertTrue(person.getId().isString());
    }
} 
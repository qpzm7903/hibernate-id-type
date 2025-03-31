package com.example.idtypedemo.repository;

import com.example.idtypedemo.config.TestConfig;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class verifies operations with STRING IDs.
 */
@DataJpaTest
@ActiveProfiles({"h2", "string-id"})
@ComponentScan(basePackages = "com.example.idtypedemo")
@Import(TestConfig.class)
public class StringPersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testSaveAndFindPersonWithStringId() {
        Person person = Person.builder()
                .id(Identifier.of("custom-id"))
                .name("Jane Doe")
                .email("jane@example.com")
                .age(25)
                .build();
        
        Person savedPerson = personRepository.save(person);
        assertNotNull(savedPerson.getId());
        assertTrue(savedPerson.getId().isString());
        assertEquals("custom-id", savedPerson.getId().asString());
        
        Person foundPerson = personRepository.findById(savedPerson.getId()).orElse(null);
        assertNotNull(foundPerson);
        assertEquals(savedPerson.getId(), foundPerson.getId());
        assertEquals("Jane Doe", foundPerson.getName());
        assertEquals("jane@example.com", foundPerson.getEmail());
        assertEquals(25, foundPerson.getAge());
    }
    
    @Test
    void testGeneratedStringId() {
        Person person = Person.builder()
                .name("Bob")
                .email("bob@example.com")
                .age(40)
                .build();
        
        Person savedPerson = personRepository.save(person);
        assertNotNull(savedPerson.getId());
        assertTrue(savedPerson.getId().isString());
        
        Person foundPerson = personRepository.findById(savedPerson.getId()).orElse(null);
        assertNotNull(foundPerson);
        assertEquals(savedPerson.getId(), foundPerson.getId());
        assertEquals("Bob", foundPerson.getName());
    }
} 
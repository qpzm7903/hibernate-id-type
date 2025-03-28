package com.example.idtypedemo.repository;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.domain.entities.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("h2")
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testSaveAndFindPersonWithLongId() {
        // Create a person with Long ID
        Person person = Person.builder()
                .id(Identifier.of(123L))
                .name("Test Person")
                .age(30)
                .build();

        // Save the person
        Person savedPerson = personRepository.save(person);
        
        // Assert that the person has an ID
        assertNotNull(savedPerson.getId());
        assertEquals(Identifier.of(123L), savedPerson.getId());
        
        // Find the person by ID
        Optional<Person> foundPerson = personRepository.findById(Identifier.of(123L));
        
        // Assert that the person is found
        assertTrue(foundPerson.isPresent());
        assertEquals("Test Person", foundPerson.get().getName());
        assertEquals(30, foundPerson.get().getAge());
    }
    
    @Test
    void testSaveAndFindPersonWithStringId() {
        // Create a person with String ID
        Person person = Person.builder()
                .id(Identifier.of("abc123"))
                .name("Test Person With String ID")
                .age(25)
                .build();

        // Save the person
        Person savedPerson = personRepository.save(person);
        
        // Assert that the person has an ID
        assertNotNull(savedPerson.getId());
        assertEquals(Identifier.of("abc123"), savedPerson.getId());
        
        // Find the person by ID
        Optional<Person> foundPerson = personRepository.findById(Identifier.of("abc123"));
        
        // Assert that the person is found
        assertTrue(foundPerson.isPresent());
        assertEquals("Test Person With String ID", foundPerson.get().getName());
        assertEquals(25, foundPerson.get().getAge());
    }
    
    @Test
    void testFindPersonByIdWithDifferentTypes() {
        // Create a person with Long ID
        Person person = Person.builder()
                .id(Identifier.of(456L))
                .name("ID Type Test")
                .age(40)
                .build();

        // Save the person
        personRepository.save(person);
        
        // Find the person using String representation
        Optional<Person> foundWithString = personRepository.findById(Identifier.of("456"));
        
        // Assert that the person is found
        assertTrue(foundWithString.isPresent());
        assertEquals("ID Type Test", foundWithString.get().getName());
        
        // Check the ID type
        assertEquals(Identifier.Type.LONG, foundWithString.get().getId().getType());
    }
} 
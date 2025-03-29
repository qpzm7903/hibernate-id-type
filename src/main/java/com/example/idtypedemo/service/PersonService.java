package com.example.idtypedemo.service;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import com.example.idtypedemo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Person entities.
 */
@Service
@Transactional
public class PersonService {
    
    private final PersonRepository personRepository;
    
    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    
    /**
     * Save a person entity.
     */
    public Person save(Person person) {
        return personRepository.save(person);
    }
    
    /**
     * Find a person by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Person> findById(Identifier id) {
        return personRepository.findById(id);
    }
    
    /**
     * Find all persons.
     */
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personRepository.findAll();
    }
    
    /**
     * Find persons by name.
     */
    @Transactional(readOnly = true)
    public List<Person> findByName(String name) {
        return personRepository.findByName(name);
    }
    
    /**
     * Delete a person by ID.
     */
    public void deleteById(Identifier id) {
        personRepository.deleteById(id);
    }
    
    /**
     * Custom method to demonstrate using the custom Identifier type.
     */
    @Transactional(readOnly = true)
    public Person findByCustomId(Identifier id) {
        return personRepository.findByIdentifier(id);
    }
} 
package com.example.idtypedemo.service;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.domain.entities.Person;
import com.example.idtypedemo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> findById(Identifier id) {
        return personRepository.findById(id);
    }
    
    // Convenience method for Long IDs
    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return id == null ? Optional.empty() : personRepository.findById(Identifier.of(id));
    }
    
    // Convenience method for String IDs
    @Transactional(readOnly = true)
    public Optional<Person> findById(String id) {
        return id == null ? Optional.empty() : personRepository.findById(Identifier.of(id));
    }

    @Transactional
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Transactional
    public void deleteById(Identifier id) {
        personRepository.deleteById(id);
    }
    
    // Convenience method for Long IDs
    @Transactional
    public void deleteById(Long id) {
        if (id != null) {
            personRepository.deleteById(Identifier.of(id));
        }
    }
    
    // Convenience method for String IDs
    @Transactional
    public void deleteById(String id) {
        if (id != null) {
            personRepository.deleteById(Identifier.of(id));
        }
    }
} 
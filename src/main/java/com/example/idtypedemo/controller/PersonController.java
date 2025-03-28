package com.example.idtypedemo.controller;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.domain.entities.Person;
import com.example.idtypedemo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        return ResponseEntity.ok(personService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable String id) {
        // Try to parse as Long first, otherwise use as String
        try {
            Long longId = Long.parseLong(id);
            return personService.findById(longId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NumberFormatException e) {
            return personService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        person.setId(null); // Ensure we're creating a new entity
        return new ResponseEntity<>(personService.save(person), HttpStatus.CREATED);
    }
    
    @PostMapping("/long/{id}")
    public ResponseEntity<Person> createPersonWithLongId(@PathVariable Long id, @RequestBody Person person) {
        person.setId(Identifier.of(id));
        return new ResponseEntity<>(personService.save(person), HttpStatus.CREATED);
    }
    
    @PostMapping("/string/{id}")
    public ResponseEntity<Person> createPersonWithStringId(@PathVariable String id, @RequestBody Person person) {
        person.setId(Identifier.of(id));
        return new ResponseEntity<>(personService.save(person), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable String id, @RequestBody Person person) {
        Identifier identifier;
        
        try {
            Long longId = Long.parseLong(id);
            identifier = Identifier.of(longId);
        } catch (NumberFormatException e) {
            identifier = Identifier.of(id);
        }
        
        Identifier finalIdentifier = identifier;
        return personService.findById(finalIdentifier)
                .map(existingPerson -> {
                    person.setId(finalIdentifier);
                    return ResponseEntity.ok(personService.save(person));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        try {
            Long longId = Long.parseLong(id);
            if (personService.findById(longId).isPresent()) {
                personService.deleteById(longId);
                return ResponseEntity.noContent().build();
            }
        } catch (NumberFormatException e) {
            if (personService.findById(id).isPresent()) {
                personService.deleteById(id);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
} 
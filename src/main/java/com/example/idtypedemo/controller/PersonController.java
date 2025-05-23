package com.example.idtypedemo.controller;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import com.example.idtypedemo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Person> all = personService.findAll();
        List<Person> sortedPersons = all.stream().sorted(Comparator.comparing(Person::getId)).collect(Collectors.toList());
        return ResponseEntity.ok(sortedPersons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Identifier id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        // Don't set ID to null, respect the provided ID
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
    public ResponseEntity<Person> updatePerson(@PathVariable Identifier id, @RequestBody Person person) {
        return personService.findById(id)
                .map(existingPerson -> {
                    person.setId(id);
                    return ResponseEntity.ok(personService.save(person));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Identifier id) {
        if (personService.findById(id).isPresent()) {
            personService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
} 
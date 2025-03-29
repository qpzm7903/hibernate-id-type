package com.example.idtypedemo.controller;

import com.example.idtypedemo.domain.view.PersonView;
import com.example.idtypedemo.entity.Person;
import com.example.idtypedemo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to demonstrate handling of nested Person objects.
 */
@RestController
@RequestMapping("/api/person-views")
public class PersonViewController {

    private final PersonService personService;

    @Autowired
    public PersonViewController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<PersonView> createPersonView(@RequestBody PersonView personView) {
        // Save the nested person entity
        if (personView.getPerson() != null) {
            personView.setPerson(personService.save(personView.getPerson()));
        }
        return new ResponseEntity<>(personView, HttpStatus.CREATED);
    }

    @GetMapping("/example")
    public ResponseEntity<PersonView> getExamplePersonView() {
        // Create an example PersonView for documentation/testing
        return ResponseEntity.ok(PersonView.builder()
                .person(Person.builder().name("Example Person").build())
                .additionalInfo("Example person view")
                .build());
    }
} 
package com.example.idtypedemo.controller;

import com.example.idtypedemo.config.TestConfig;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import com.example.idtypedemo.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @Test
    void shouldCreatePersonWithStringId() throws Exception {
        // Create request body
        String requestBody = """
                {
                    "id": "testId",
                    "name": "testName",
                    "age": 30
                }""";

        // Perform request
        MvcResult result = mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse response
        Person createdPerson = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Person.class
        );

        // Verify response
        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId());
        assertEquals("testId", createdPerson.getId().asString());
        assertEquals("testName", createdPerson.getName());
        assertEquals(30, createdPerson.getAge());
        assertEquals(Identifier.Type.STRING, createdPerson.getId().getType());
    }

    @Test
    void shouldCreatePersonWithLongId() throws Exception {
        // Create request body
        String requestBody = """
                {
                    "id": "123",
                    "name": "testName",
                    "age": 30
                }""";

        // Perform request
        MvcResult result = mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse response
        Person createdPerson = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Person.class
        );

        // Verify response
        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId());
        assertEquals(123L, createdPerson.getId().asLong());
        assertEquals("testName", createdPerson.getName());
        assertEquals(30, createdPerson.getAge());
        assertEquals(Identifier.Type.LONG, createdPerson.getId().getType());
    }

    @Test
    void shouldCreatePersonWithoutId() throws Exception {
        // Create request body
        String requestBody = """
                {
                    "name": "testName",
                    "age": 30
                }""";

        // Perform request
        MvcResult result = mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse response
        Person createdPerson = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Person.class
        );

        // Verify response
        assertNotNull(createdPerson);
        assertNotNull(createdPerson.getId()); // Should have generated ID
        assertEquals("testName", createdPerson.getName());
        assertEquals(30, createdPerson.getAge());
    }
    
    @Test
    void shouldFindPersonById() throws Exception {
        // First create a person to retrieve
        Person person = new Person();
        person.setName("Test User");
        person.setAge(25);
        person.setId(Identifier.of("test-find-id"));
        Person savedPerson = personService.save(person);
        
        // Test retrieving with String ID
        mockMvc.perform(get("/api/persons/{id}", savedPerson.getId().asString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPerson.getId().asString()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.age").value(25));
    }
    
    @Test
    void shouldFindPersonByLongId() throws Exception {
        // First create a person with Long ID
        Person person = new Person();
        person.setName("Number User");
        person.setAge(40);
        person.setId(Identifier.of(9999L));
        Person savedPerson = personService.save(person);
        
        // Test retrieving with Long ID
        mockMvc.perform(get("/api/persons/{id}", savedPerson.getId().asLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPerson.getId().asString()))
                .andExpect(jsonPath("$.name").value("Number User"))
                .andExpect(jsonPath("$.age").value(40));
    }
    
    @Test
    void shouldUpdatePerson() throws Exception {
        // First create a person to update
        Person person = new Person();
        person.setName("Original Name");
        person.setAge(30);
        Person savedPerson = personService.save(person);
        
        // Create update request body
        String requestBody = String.format("""
                {
                    "name": "Updated Name",
                    "age": 35
                }""");
        
        // Perform update request
        mockMvc.perform(put("/api/persons/{id}", savedPerson.getId().asString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPerson.getId().asString()))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.age").value(35));
    }
    
    @Test
    void shouldDeletePerson() throws Exception {
        // First create a person to delete
        Person person = new Person();
        person.setName("Delete Me");
        person.setAge(50);
        Person savedPerson = personService.save(person);
        
        // Delete the person
        mockMvc.perform(delete("/api/persons/{id}", savedPerson.getId().asString()))
                .andExpect(status().isNoContent());
        
        // Verify person is deleted
        mockMvc.perform(get("/api/persons/{id}", savedPerson.getId().asString()))
                .andExpect(status().isNotFound());
    }
} 
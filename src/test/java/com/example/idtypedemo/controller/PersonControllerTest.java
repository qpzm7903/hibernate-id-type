package com.example.idtypedemo.controller;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import com.example.idtypedemo.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
} 
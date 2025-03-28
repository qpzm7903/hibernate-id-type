package com.example.idtypedemo.controller;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.domain.entities.Person;
import com.example.idtypedemo.domain.view.PersonView;
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
class PersonViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonService personService;

    @Test
    void shouldCreatePersonViewWithStringId() throws Exception {
        // Create request body with nested person
        String requestBody = """
                {
                    "person": {
                        "id": "testId",
                        "name": "testName",
                        "age": 30
                    },
                    "additionalInfo": "Test Info"
                }""";

        // Perform request
        MvcResult result = mockMvc.perform(post("/api/person-views")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse response
        PersonView createdView = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PersonView.class
        );

        // Verify response
        assertNotNull(createdView);
        assertNotNull(createdView.getPerson());
        assertEquals("Test Info", createdView.getAdditionalInfo());
        
        // Verify nested person
        Person person = createdView.getPerson();
        assertNotNull(person.getId());
        assertEquals("testId", person.getId().asString());
        assertEquals("testName", person.getName());
        assertEquals(30, person.getAge());
        assertEquals(Identifier.Type.STRING, person.getId().getType());
    }

    @Test
    void shouldCreatePersonViewWithLongId() throws Exception {
        // Create request body with nested person
        String requestBody = """
                {
                    "person": {
                        "id": "123",
                        "name": "testName",
                        "age": 30
                    },
                    "additionalInfo": "Test Info"
                }""";

        // Perform request
        MvcResult result = mockMvc.perform(post("/api/person-views")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse response
        PersonView createdView = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PersonView.class
        );

        // Verify response
        assertNotNull(createdView);
        assertNotNull(createdView.getPerson());
        assertEquals("Test Info", createdView.getAdditionalInfo());
        
        // Verify nested person
        Person person = createdView.getPerson();
        assertNotNull(person.getId());
        assertEquals(123L, person.getId().asLong());
        assertEquals("testName", person.getName());
        assertEquals(30, person.getAge());
        assertEquals(Identifier.Type.LONG, person.getId().getType());
    }

    @Test
    void shouldCreatePersonViewWithoutPerson() throws Exception {
        // Create request body without person
        String requestBody = """
                {
                    "additionalInfo": "Test Info"
                }""";

        // Perform request
        MvcResult result = mockMvc.perform(post("/api/person-views")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse response
        PersonView createdView = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PersonView.class
        );

        // Verify response
        assertNotNull(createdView);
        assertNull(createdView.getPerson());
        assertEquals("Test Info", createdView.getAdditionalInfo());
    }

    @Test
    void shouldCreatePersonViewWithPersonWithoutId() throws Exception {
        // Create request body with person without id
        String requestBody = """
                {
                    "person": {
                        "name": "testName",
                        "age": 30
                    },
                    "additionalInfo": "Test Info"
                }""";

        // Perform request
        MvcResult result = mockMvc.perform(post("/api/person-views")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Parse response
        PersonView createdView = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                PersonView.class
        );

        // Verify response
        assertNotNull(createdView);
        assertNotNull(createdView.getPerson());
        assertEquals("Test Info", createdView.getAdditionalInfo());
        
        // Verify nested person
        Person person = createdView.getPerson();
        assertNotNull(person.getId()); // Should have generated ID
        assertEquals("testName", person.getName());
        assertEquals(30, person.getAge());
    }
} 
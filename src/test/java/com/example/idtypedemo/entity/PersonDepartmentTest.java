package com.example.idtypedemo.entity;

import com.example.idtypedemo.config.TestConfig;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.repository.DepartmentRepository;
import com.example.idtypedemo.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for the ManyToOne relationship between Person and Department entities.
 */
@DataJpaTest
@ActiveProfiles("h2")
@Import(TestConfig.class)
public class PersonDepartmentTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Test creating a Person associated with a Department and verifying the relationship.
     */
    @Test
    @Transactional
    public void testPersonDepartmentRelationship() {
        // Create a Department
        Department department = Department.builder()
                .name("Engineering")
                .description("Software Engineering Department")
                .build();
        departmentRepository.save(department);
        
        assertNotNull(department.getId(), "Department ID should not be null");
        
        // Create a Person associated with the Department
        Person person = Person.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .age(30)
                .department(department)
                .build();
        personRepository.save(person);
        
        assertNotNull(person.getId(), "Person ID should not be null");
        
        // Verify the relationship
        Person foundPerson = personRepository.findById(person.getId()).orElse(null);
        assertNotNull(foundPerson, "Person should be found");
        assertNotNull(foundPerson.getDepartment(), "Person's department should not be null");
        assertEquals(department.getId(), foundPerson.getDepartment().getId(), 
                "Person should be associated with the correct department");
        assertEquals("Engineering", foundPerson.getDepartment().getName(),
                "Department name should match");
    }
    
    /**
     * Test updating a Person's Department association.
     */
    @Test
    @Transactional
    public void testUpdatePersonDepartment() {
        // Create two departments
        Department department1 = Department.builder()
                .name("Engineering")
                .description("Software Engineering Department")
                .build();
        departmentRepository.save(department1);
        
        Department department2 = Department.builder()
                .name("Marketing")
                .description("Marketing Department")
                .build();
        departmentRepository.save(department2);
        
        // Create a Person associated with the first Department
        Person person = Person.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .age(30)
                .department(department1)
                .build();
        personRepository.save(person);
        
        // Update Person's department
        person.setDepartment(department2);
        personRepository.save(person);
        
        // Verify the updated relationship
        Person updatedPerson = personRepository.findById(person.getId()).orElse(null);
        assertNotNull(updatedPerson, "Person should be found");
        assertNotNull(updatedPerson.getDepartment(), "Person's department should not be null");
        assertEquals(department2.getId(), updatedPerson.getDepartment().getId(), 
                "Person should be associated with the updated department");
        assertEquals("Marketing", updatedPerson.getDepartment().getName(),
                "Department name should match the new department");
    }
} 
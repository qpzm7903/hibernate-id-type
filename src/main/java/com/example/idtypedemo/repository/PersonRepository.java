package com.example.idtypedemo.repository;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Person entity using the Identifier type.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Identifier> {
    
    /**
     * Find persons by name.
     */
    List<Person> findByName(String name);
    
    /**
     * Find persons by email.
     */
    List<Person> findByEmail(String email);
    
    /**
     * Find persons with age greater than the specified value.
     */
    List<Person> findByAgeGreaterThan(Integer age);
    
    /**
     * Custom query to demonstrate using the Identifier type in queries.
     */
    @Query("SELECT p FROM Person p WHERE p.id = ?1")
    Person findByIdentifier(Identifier id);
} 
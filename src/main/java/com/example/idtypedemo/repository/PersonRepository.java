package com.example.idtypedemo.repository;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.domain.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Identifier> {
    // Custom query methods can be added here
} 
package com.example.idtypedemo.repository;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Department} entity operations.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Identifier> {
    
    /**
     * Find a department by its name.
     *
     * @param name The name of the department to find
     * @return The department with the given name, if found
     */
    Department findByName(String name);
} 
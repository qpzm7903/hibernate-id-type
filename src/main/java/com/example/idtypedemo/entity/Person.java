package com.example.idtypedemo.entity;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.type.IdentifierType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.GeneratedValue;

/**
 * Example entity that uses the Identifier type for its ID field.
 * The column definition is resolved at runtime based on configuration.
 */
@Entity
@Table(name = "person")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    
    @Id
    @Type(IdentifierType.class)
    @Column(name = "id")
    @GeneratedValue(generator = "custom-identifier")
    @GenericGenerator(name = "custom-identifier", strategy = "com.example.idtypedemo.type.CustomIdentifierGenerator")
    private Identifier id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "age")
    private Integer age;
} 
package com.example.idtypedemo.entity;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.type.IdentifierType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.GeneratedValue;

/**
 * Department entity representing a department within the organization.
 * Each department can have multiple people associated with it.
 */
@Entity
@Table(name = "department")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    
    @Id
    @Type(IdentifierType.class)
    @Column(name = "id")
    @GeneratedValue(generator = "custom-identifier")
    @GenericGenerator(name = "custom-identifier", strategy = "com.example.idtypedemo.type.CustomIdentifierGenerator")
    private Identifier id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Builder.Default
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Person> people = new ArrayList<>();
} 
package com.example.idtypedemo.domain.entities;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.type.IdentifierType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    
    @Id
    @Type(IdentifierType.class)
    private Identifier id;
    
    private String name;
    
    private Integer age;
    
    // Convenience method to create a person with a Long ID
    public static Person withLongId(Long id, String name, Integer age) {
        return Person.builder()
                .id(id != null ? Identifier.of(id) : null)
                .name(name)
                .age(age)
                .build();
    }
    
    // Convenience method to create a person with a String ID
    public static Person withStringId(String id, String name, Integer age) {
        return Person.builder()
                .id(id != null ? Identifier.of(id) : null)
                .name(name)
                .age(age)
                .build();
    }
} 
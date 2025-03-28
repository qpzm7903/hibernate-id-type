package com.example.idtypedemo.domain.entities;

import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.type.IdentifierType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class Person {
    
    @Id
    @Type(IdentifierType.class)
    @GeneratedValue(generator = "custom-identifier")
    @GenericGenerator(name = "custom-identifier", strategy = "com.example.idtypedemo.type.CustomIdentifierGenerator")
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
    
    // JSON constructor for deserialization
    @JsonCreator
    public static Person fromJson(
            @JsonProperty("id") Identifier id,
            @JsonProperty("name") String name,
            @JsonProperty("age") Integer age) {
        return Person.builder()
                .id(id)
                .name(name)
                .age(age)
                .build();
    }
} 
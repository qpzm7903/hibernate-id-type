package com.example.idtypedemo.domain.view;

import com.example.idtypedemo.domain.entities.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * View class that wraps a Person entity.
 * Used to demonstrate nested JSON serialization/deserialization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class PersonView {
    private Person person;
    private String additionalInfo;
} 
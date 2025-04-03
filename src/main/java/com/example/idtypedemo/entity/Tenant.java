package com.example.idtypedemo.entity;


import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.type.IdentifierType;
import jakarta.persistence.Embeddable;
import lombok.Data;
import org.hibernate.annotations.Type;

@Embeddable
@Data
public class Tenant {
    @Type(IdentifierType.class)
    private Identifier id;
}

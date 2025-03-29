package com.example.idtypedemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.example.idtypedemo.domain.Identifier;

/**
 * Configuration properties for the Identifier type system.
 */
@Configuration
@ConfigurationProperties(prefix = "identifier")
public class IdentifierTypeConfig {
    
    /**
     * The type of ID to use (LONG or STRING)
     */
    private Identifier.Type type = Identifier.Type.LONG;
    
    /**
     * The maximum length for string IDs
     */
    private int stringLength = 255;

    public Identifier.Type getType() {
        return type;
    }

    public void setType(Identifier.Type type) {
        this.type = type;
    }

    public int getStringLength() {
        return stringLength;
    }

    public void setStringLength(int stringLength) {
        this.stringLength = stringLength;
    }
} 
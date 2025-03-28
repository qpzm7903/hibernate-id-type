package com.example.idtypedemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the Identifier system.
 * These properties can be set in application.properties/yaml.
 */
@Configuration
@ConfigurationProperties(prefix = "identifier")
public class IdentifierProperties {
    
    /**
     * The default type to use for identifiers when not specified.
     * Possible values: LONG, STRING
     */
    private String defaultType = "LONG";
    
    /**
     * Whether to use string representation for equality checks.
     * If true, "123" and 123L will be considered equal.
     * If false, they will be considered different.
     */
    private boolean stringEqualityCheck = true;
    
    /**
     * Whether to automatically try to convert string values to long when possible.
     */
    private boolean autoConvertStringToLong = true;

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public boolean isStringEqualityCheck() {
        return stringEqualityCheck;
    }

    public void setStringEqualityCheck(boolean stringEqualityCheck) {
        this.stringEqualityCheck = stringEqualityCheck;
    }

    public boolean isAutoConvertStringToLong() {
        return autoConvertStringToLong;
    }

    public void setAutoConvertStringToLong(boolean autoConvertStringToLong) {
        this.autoConvertStringToLong = autoConvertStringToLong;
    }
} 
package com.example.idtypedemo.type;

import java.sql.Types;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.idtypedemo.domain.Identifier;

/**
 * Default implementation of DatabaseTypeResolver that handles type mapping
 * for different database dialects.
 */
@Component
public class DefaultDatabaseTypeResolver implements DatabaseTypeResolver {

    @Value("${identifier.string.length:255}")
    private int stringLength;

    @Override
    public int resolveSqlType(Identifier.Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Identifier type cannot be null");
        }
        
        return switch (type) {
            case LONG -> Types.BIGINT;
            case STRING -> Types.VARCHAR;
        };
    }

    @Override
    public String getColumnDefinition(Identifier.Type type, String dialect) {
        if (type == null) {
            throw new IllegalArgumentException("Identifier type cannot be null");
        }
        if (dialect == null || dialect.trim().isEmpty()) {
            throw new IllegalArgumentException("Database dialect cannot be null or empty");
        }

        return switch (type) {
            case LONG -> getNumericColumnDefinition(dialect);
            case STRING -> getStringColumnDefinition(dialect);
        };
    }

    private String getNumericColumnDefinition(String dialect) {
        return switch (dialect.toLowerCase()) {
            case "mysql", "h2", "postgresql" -> "BIGINT";
            default -> throw new UnsupportedOperationException(
                "Unsupported database dialect: " + dialect);
        };
    }

    private String getStringColumnDefinition(String dialect) {
        return switch (dialect.toLowerCase()) {
            case "mysql", "h2", "postgresql" -> 
                String.format("VARCHAR(%d)", stringLength);
            default -> throw new UnsupportedOperationException(
                "Unsupported database dialect: " + dialect);
        };
    }
} 
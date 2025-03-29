package com.example.idtypedemo.type;

import java.sql.Types;
import com.example.idtypedemo.domain.Identifier;

/**
 * Resolves database column types based on the configured Identifier type.
 * This interface provides methods to determine the appropriate SQL type and column definition
 * for different database dialects.
 */
public interface DatabaseTypeResolver {
    
    /**
     * Resolves the SQL type code based on the Identifier type.
     * @param type The Identifier type (LONG or STRING)
     * @return The SQL type code from java.sql.Types
     */
    int resolveSqlType(Identifier.Type type);
    
    /**
     * Gets the database-specific column definition for the given Identifier type.
     * @param type The Identifier type (LONG or STRING)
     * @param dialect The database dialect being used
     * @return The appropriate column definition string
     */
    String getColumnDefinition(Identifier.Type type, String dialect);
} 
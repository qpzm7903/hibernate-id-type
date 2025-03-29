package com.example.idtypedemo.type;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.usertype.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Custom Hibernate type for the Identifier class.
 * Maps the Identifier to the appropriate database column type (BIGINT or VARCHAR)
 * based on configuration settings.
 */
@Component
public class IdentifierType implements UserType<Identifier> {

    // Constants for type discriminators
    public static final String LONG_TYPE_PREFIX = "__LONG:";
    public static final String STRING_TYPE_PREFIX = "__STRING:";
    
    private static DatabaseTypeResolver databaseTypeResolver;
    private static IdentifierProperties identifierProperties;
    
    @Value("${identifier.use.native.types:true}")
    private boolean useNativeTypes = true;
    
    @Value("${hibernate.dialect:}")
    private String hibernateDialect;
    
    // Component to inject dependencies
    @Component
    static class IdentifierTypeInjector {
        @Autowired
        public IdentifierTypeInjector(DatabaseTypeResolver resolver, IdentifierProperties props) {
            IdentifierType.databaseTypeResolver = resolver;
            IdentifierType.identifierProperties = props;
        }
    }

    @Override
    public int getSqlType() {
        if (databaseTypeResolver != null && useNativeTypes) {
            Identifier.Type idType = Identifier.Type.valueOf(identifierProperties.getDefaultType().toUpperCase());
            return databaseTypeResolver.resolveSqlType(idType);
        }
        // Fallback to VARCHAR if config not available
        return Types.VARCHAR;
    }

    @Override
    public Class<Identifier> returnedClass() {
        return Identifier.class;
    }

    @Override
    public boolean equals(Identifier x, Identifier y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Identifier x) {
        return Objects.hashCode(x);
    }

    @Override
    public Identifier nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) 
            throws SQLException {
        
        // For Long-configured system, try to read as BIGINT first
        if (isLongTypeSystem() && useNativeTypes) {
            Long longValue = rs.getLong(position);
            if (!rs.wasNull()) {
                return Identifier.of(longValue);
            }
            // If null, fall through to null handling below
        } 
        // Otherwise, read as VARCHAR
        else {
            String value = rs.getString(position);
            if (value != null) {
                if (value.startsWith(LONG_TYPE_PREFIX)) {
                    String longValue = value.substring(LONG_TYPE_PREFIX.length());
                    return Identifier.of(Long.valueOf(longValue));
                } else if (value.startsWith(STRING_TYPE_PREFIX)) {
                    return Identifier.of(value.substring(STRING_TYPE_PREFIX.length()));
                }
                
                // For backward compatibility, try to parse as Long if no prefix
                try {
                    return Identifier.of(Long.valueOf(value));
                } catch (NumberFormatException e) {
                    return Identifier.of(value);
                }
            }
        }
        
        // Handle null case
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Identifier value, int index, SharedSessionContractImplementor session) 
            throws SQLException {
        
        if (value == null) {
            st.setNull(index, getSqlType());
            return;
        }
        
        // For Long-configured system with native types, use direct BIGINT for Long values
        if (isLongTypeSystem() && useNativeTypes && value.isLong()) {
            st.setLong(index, value.asLong());
        } 
        // For String-configured system with native types, use direct VARCHAR for String values
        else if (isStringTypeSystem() && useNativeTypes && value.isString()) {
            st.setString(index, value.asString());
        }
        // Otherwise, use type prefixes with VARCHAR
        else {
            if (value.getType() == Identifier.Type.LONG) {
                st.setString(index, LONG_TYPE_PREFIX + value.asLong());
            } else {
                st.setString(index, STRING_TYPE_PREFIX + value.asString());
            }
        }
    }

    @Override
    public Identifier deepCopy(Identifier value) {
        return value; // Identifier is immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Identifier value) {
        if (value == null) {
            return null;
        }
        
        if (value.getType() == Identifier.Type.LONG) {
            return LONG_TYPE_PREFIX + value.asLong();
        } else {
            return STRING_TYPE_PREFIX + value.asString();
        }
    }

    @Override
    public Identifier assemble(Serializable cached, Object owner) {
        if (cached == null) {
            return null;
        }
        
        String value = cached.toString();
        if (value.startsWith(LONG_TYPE_PREFIX)) {
            return Identifier.of(Long.valueOf(value.substring(LONG_TYPE_PREFIX.length())));
        } else if (value.startsWith(STRING_TYPE_PREFIX)) {
            return Identifier.of(value.substring(STRING_TYPE_PREFIX.length()));
        }
        
        // For backward compatibility
        try {
            return Identifier.of(Long.valueOf(value));
        } catch (NumberFormatException e) {
            return Identifier.of(value);
        }
    }

    @Override
    public Identifier replace(Identifier original, Identifier target, Object owner) {
        return original; // Identifier is immutable
    }

    /**
     * Get the column definition based on the configured ID type and dialect.
     * This can be used in @Column annotations.
     */
    public static String getColumnDefinition() {
        if (databaseTypeResolver != null && identifierProperties != null) {
            String dialect = getCurrentDialect();
            Identifier.Type type = Identifier.Type.valueOf(identifierProperties.getDefaultType().toUpperCase());
            return databaseTypeResolver.getColumnDefinition(type, dialect);
        }
        return "VARCHAR(255)"; // Default fallback
    }
    
    /**
     * Check if the system is configured to use Long IDs
     */
    private boolean isLongTypeSystem() {
        return identifierProperties != null && "LONG".equalsIgnoreCase(identifierProperties.getDefaultType());
    }
    
    /**
     * Check if the system is configured to use String IDs
     */
    private boolean isStringTypeSystem() {
        return identifierProperties != null && "STRING".equalsIgnoreCase(identifierProperties.getDefaultType());
    }
    
    /**
     * Get the current Hibernate dialect
     */
    private static String getCurrentDialect() {
        // Extract dialect name from full class name
        String fullDialect = System.getProperty("hibernate.dialect");
        if (fullDialect == null || fullDialect.isEmpty()) {
            return "h2"; // Default to H2
        }
        
        String dialectLower = fullDialect.toLowerCase();
        if (dialectLower.contains("mysql")) {
            return "mysql";
        } else if (dialectLower.contains("postgresql")) {
            return "postgresql";
        } else if (dialectLower.contains("h2")) {
            return "h2";
        }
        
        return "h2"; // Default to H2
    }
} 
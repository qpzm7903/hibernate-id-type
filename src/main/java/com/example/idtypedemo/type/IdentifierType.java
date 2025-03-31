package com.example.idtypedemo.type;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.usertype.UserType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class IdentifierType implements UserType<Identifier>, ApplicationContextAware {
    
    // Static reference to application context for access in no-args constructor
    private static ApplicationContext applicationContext;
    
    private DatabaseTypeResolver databaseTypeResolver;
    private IdentifierProperties identifierProperties;
    
    @Value("${identifier.use.native.types:true}")
    private boolean useNativeTypes = true;
    
    @Value("${hibernate.dialect:}")
    private String hibernateDialect;

    /**
     * No-args constructor required by Hibernate for direct instantiation.
     * Dependencies will be lazily loaded from application context.
     */
    public IdentifierType() {
        // Will be initialized lazily through getDatabaseTypeResolver() and getIdentifierProperties()
    }

    @Autowired
    public IdentifierType(DatabaseTypeResolver databaseTypeResolver, IdentifierProperties identifierProperties) {
        this.databaseTypeResolver = Objects.requireNonNull(databaseTypeResolver, "DatabaseTypeResolver must not be null");
        this.identifierProperties = Objects.requireNonNull(identifierProperties, "IdentifierProperties must not be null");
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // Store static reference to application context
        IdentifierType.applicationContext = applicationContext;
    }
    
    /**
     * Lazy getter for database type resolver
     */
    private DatabaseTypeResolver getDatabaseTypeResolver() {
        if (databaseTypeResolver == null && applicationContext != null) {
            databaseTypeResolver = applicationContext.getBean(DatabaseTypeResolver.class);
        }
        
        if (databaseTypeResolver == null) {
            // Fallback to default implementation if application context not available
            databaseTypeResolver = new DefaultDatabaseTypeResolver();
        }
        
        return databaseTypeResolver;
    }
    
    /**
     * Lazy getter for identifier properties
     */
    private IdentifierProperties getIdentifierProperties() {
        if (identifierProperties == null && applicationContext != null) {
            identifierProperties = applicationContext.getBean(IdentifierProperties.class);
        }
        
        if (identifierProperties == null) {
            // Fallback to default settings if application context not available
            IdentifierProperties defaultProps = new IdentifierProperties();
            defaultProps.setDefaultType("LONG");
            defaultProps.setStringEqualityCheck(true);
            defaultProps.setAutoConvertStringToLong(true);
            identifierProperties = defaultProps;
        }
        
        return identifierProperties;
    }

    @Override
    public int getSqlType() {
        if (useNativeTypes) {
            Identifier.Type idType = Identifier.Type.valueOf(getIdentifierProperties().getDefaultType().toUpperCase());
            return getDatabaseTypeResolver().resolveSqlType(idType);
        }
        // Fallback to VARCHAR if native types disabled
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
                // Try to parse as Long if appropriate
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
        // Otherwise, use simple string representation
        else {
            st.setString(index, value.asString());
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
        
        return value.asString();
    }

    @Override
    public Identifier assemble(Serializable cached, Object owner) {
        if (cached == null) {
            return null;
        }
        
        String value = cached.toString();
        
        // Try to parse as Long if possible
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
    public String getColumnDefinition() {
        String dialect = hibernateDialect;
        Identifier.Type type = Identifier.Type.valueOf(getIdentifierProperties().getDefaultType().toUpperCase());
        return getDatabaseTypeResolver().getColumnDefinition(type, dialect);
    }
    
    /**
     * Check if the system is configured to use Long IDs
     */
    private boolean isLongTypeSystem() {
        return getIdentifierProperties() != null && "LONG".equalsIgnoreCase(getIdentifierProperties().getDefaultType());
    }
    
    /**
     * Check if the system is configured to use String IDs
     */
    private boolean isStringTypeSystem() {
        return getIdentifierProperties() != null && "STRING".equalsIgnoreCase(getIdentifierProperties().getDefaultType());
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
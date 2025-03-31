package com.example.idtypedemo.type;

import com.example.idtypedemo.config.ConfigurationLoader;
import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
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
import java.util.logging.Logger;

/**
 * Custom Hibernate type for the Identifier class.
 * Maps the Identifier to the appropriate database column type (BIGINT or VARCHAR)
 * based on configuration settings.
 * <p>
 * This implementation loads configuration directly from property files for maximum compatibility
 * across different deployment scenarios.
 */
@Component
public class IdentifierType implements UserType<Identifier> {
    
    private static final Logger logger = Logger.getLogger(IdentifierType.class.getName());
    
    // Default values for configuration
    private static final String DEFAULT_ID_TYPE = "LONG";
    private static final boolean DEFAULT_USE_NATIVE_TYPES = true;
    private static final boolean DEFAULT_STRING_EQUALITY_CHECK = true;
    private static final boolean DEFAULT_AUTO_CONVERT = true;
    
    // Lazy-initialized dependencies
    private DatabaseTypeResolver databaseTypeResolver;
    private IdentifierProperties identifierProperties;
    
    @Value("${identifier.use.native.types:true}")
    private boolean useNativeTypes = true;

    /**
     * No-args constructor required by Hibernate for direct instantiation.
     * Dependencies will be lazily loaded from properties files.
     */
    public IdentifierType() {
        logger.fine("IdentifierType instantiated via no-args constructor");
    }

    /**
     * Spring-managed constructor with injected dependencies.
     */
    @Autowired
    public IdentifierType(DatabaseTypeResolver databaseTypeResolver, IdentifierProperties identifierProperties) {
        logger.fine("IdentifierType instantiated via Spring DI constructor");
        this.databaseTypeResolver = Objects.requireNonNull(databaseTypeResolver, "DatabaseTypeResolver must not be null");
        this.identifierProperties = Objects.requireNonNull(identifierProperties, "IdentifierProperties must not be null");
    }

    /**
     * Gets database type resolver, creating a default one if needed.
     */
    private DatabaseTypeResolver getDatabaseTypeResolver() {
        if (databaseTypeResolver == null) {
            logger.fine("Creating default DatabaseTypeResolver");
            databaseTypeResolver = new DefaultDatabaseTypeResolver();
        }
        return databaseTypeResolver;
    }
    
    /**
     * Gets identifier properties, creating a default one from properties if needed.
     */
    private IdentifierProperties getIdentifierProperties() {
        if (identifierProperties == null) {
            logger.fine("Creating IdentifierProperties from properties");
            IdentifierProperties defaultProps = new IdentifierProperties();
            
            // Load values from the configuration loader
            String defaultType = ConfigurationLoader.getProperty("identifier.type", DEFAULT_ID_TYPE);
            boolean stringEqualityCheck = ConfigurationLoader.getBooleanProperty(
                    "identifier.string.equality.check", DEFAULT_STRING_EQUALITY_CHECK);
            boolean autoConvert = ConfigurationLoader.getBooleanProperty(
                    "identifier.auto.convert.string.to.long", DEFAULT_AUTO_CONVERT);
            
            defaultProps.setDefaultType(defaultType);
            defaultProps.setStringEqualityCheck(stringEqualityCheck);
            defaultProps.setAutoConvertStringToLong(autoConvert);
            identifierProperties = defaultProps;
            
            logger.fine("Created IdentifierProperties with type=" + defaultType + 
                         ", stringEqualityCheck=" + stringEqualityCheck + 
                         ", autoConvert=" + autoConvert);
        }
        
        return identifierProperties;
    }

    /**
     * Gets the value of useNativeTypes property.
     * Tries to use the value injected by Spring, otherwise falls back to direct properties.
     */
    private boolean isUseNativeTypes() {
        // If Spring has not injected a value (i.e., it's still the default),
        // try to get it from configuration loader
        if (this.useNativeTypes == DEFAULT_USE_NATIVE_TYPES) {
            return ConfigurationLoader.getBooleanProperty("identifier.use.native.types", DEFAULT_USE_NATIVE_TYPES);
        }
        return this.useNativeTypes;
    }

    @Override
    public int getSqlType() {
        if (isUseNativeTypes()) {
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
        if (isLongTypeSystem() && isUseNativeTypes()) {
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
        if (isLongTypeSystem() && isUseNativeTypes() && value.isLong()) {
            st.setLong(index, value.asLong());
        } 
        // For String-configured system with native types, use direct VARCHAR for String values
        else if (isStringTypeSystem() && isUseNativeTypes() && value.isString()) {
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
} 
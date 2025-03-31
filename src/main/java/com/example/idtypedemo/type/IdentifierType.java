package com.example.idtypedemo.type;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.usertype.UserType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom Hibernate type for the Identifier class.
 * Maps the Identifier to the appropriate database column type (BIGINT or VARCHAR)
 * based on configuration settings.
 * <p>
 * This implementation is designed to handle Hibernate's direct instantiation process
 * and work properly even when Spring's dependency injection is bypassed.
 */
@Component
public class IdentifierType implements UserType<Identifier>, ApplicationContextAware, BeanFactoryAware {
    
    private static final Logger logger = Logger.getLogger(IdentifierType.class.getName());
    
    // Static references to Spring containers for access in no-args constructor and direct Hibernate instantiation
    private static ApplicationContext applicationContext;
    private static BeanFactory beanFactory;
    
    // Lazy-initialized dependencies
    private DatabaseTypeResolver databaseTypeResolver;
    private IdentifierProperties identifierProperties;
    
    @Value("${identifier.use.native.types:true}")
    private boolean useNativeTypes = true;
    
    @Value("${hibernate.dialect:}")
    private String hibernateDialect;

    /**
     * No-args constructor required by Hibernate for direct instantiation.
     * Dependencies will be lazily loaded from various sources.
     */
    public IdentifierType() {
        logger.fine("IdentifierType instantiated via no-args constructor");
        // Dependencies will be initialized lazily through getter methods
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
     * Initialize dependencies after construction when in a Spring context.
     */
    @PostConstruct
    public void init() {
        logger.fine("IdentifierType @PostConstruct initialization");
        // Ensure dependencies are initialized
        getDatabaseTypeResolver();
        getIdentifierProperties();
    }
    
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        logger.fine("Setting static ApplicationContext reference in IdentifierType");
        IdentifierType.applicationContext = context;
        
        // Try to eagerly initialize dependencies if not already set
        if (this.databaseTypeResolver == null) {
            try {
                this.databaseTypeResolver = context.getBean(DatabaseTypeResolver.class);
                logger.fine("Initialized DatabaseTypeResolver from ApplicationContext");
            } catch (BeansException e) {
                logger.fine("Failed to get DatabaseTypeResolver from ApplicationContext: " + e.getMessage());
                // Will be handled by lazy initialization
            }
        }
        
        if (this.identifierProperties == null) {
            try {
                this.identifierProperties = context.getBean(IdentifierProperties.class);
                logger.fine("Initialized IdentifierProperties from ApplicationContext");
            } catch (BeansException e) {
                logger.fine("Failed to get IdentifierProperties from ApplicationContext: " + e.getMessage());
                // Will be handled by lazy initialization
            }
        }
    }
    
    @Override
    public void setBeanFactory(BeanFactory factory) throws BeansException {
        logger.fine("Setting static BeanFactory reference in IdentifierType");
        IdentifierType.beanFactory = factory;
    }
    
    /**
     * Enhanced lazy getter for database type resolver with multiple fallback strategies.
     */
    private DatabaseTypeResolver getDatabaseTypeResolver() {
        if (databaseTypeResolver == null) {
            // Try multiple ways to get the dependency
            
            // 1. Try instance ApplicationContext
            if (applicationContext != null) {
                try {
                    databaseTypeResolver = applicationContext.getBean(DatabaseTypeResolver.class);
                    logger.fine("Got DatabaseTypeResolver from static ApplicationContext");
                    return databaseTypeResolver;
                } catch (BeansException e) {
                    logger.fine("Failed to get DatabaseTypeResolver from ApplicationContext");
                }
            }
            
            // 2. Try instance BeanFactory
            if (beanFactory != null) {
                try {
                    databaseTypeResolver = beanFactory.getBean(DatabaseTypeResolver.class);
                    logger.fine("Got DatabaseTypeResolver from static BeanFactory");
                    return databaseTypeResolver;
                } catch (BeansException e) {
                    logger.fine("Failed to get DatabaseTypeResolver from BeanFactory");
                }
            }
            
            // 3. Fallback to default implementation
            logger.fine("Creating default DatabaseTypeResolver as fallback");
            databaseTypeResolver = new DefaultDatabaseTypeResolver();
        }
        
        return databaseTypeResolver;
    }
    
    /**
     * Enhanced lazy getter for identifier properties with multiple fallback strategies.
     */
    private IdentifierProperties getIdentifierProperties() {
        if (identifierProperties == null) {
            // Try multiple ways to get the dependency
            
            // 1. Try instance ApplicationContext
            if (applicationContext != null) {
                try {
                    identifierProperties = applicationContext.getBean(IdentifierProperties.class);
                    logger.fine("Got IdentifierProperties from static ApplicationContext");
                    return identifierProperties;
                } catch (BeansException e) {
                    logger.fine("Failed to get IdentifierProperties from ApplicationContext");
                }
            }
            
            // 2. Try instance BeanFactory
            if (beanFactory != null) {
                try {
                    identifierProperties = beanFactory.getBean(IdentifierProperties.class);
                    logger.fine("Got IdentifierProperties from static BeanFactory");
                    return identifierProperties;
                } catch (BeansException e) {
                    logger.fine("Failed to get IdentifierProperties from BeanFactory");
                }
            }
            
            // 3. Fallback to default implementation
            logger.fine("Creating default IdentifierProperties as fallback");
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
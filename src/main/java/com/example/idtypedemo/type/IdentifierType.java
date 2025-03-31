package com.example.idtypedemo.type;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.usertype.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
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
    
    // Configuration file paths to try
    private static final String[] CONFIG_LOCATIONS = {
            "classpath:application.properties",
            "classpath:application.yml",
            "classpath:application-${spring.profiles.active}.properties",
            "file:./application.properties",
            "file:./config/application.properties"
    };
    
    // Default values for configuration
    private static final String DEFAULT_ID_TYPE = "LONG";
    private static final boolean DEFAULT_USE_NATIVE_TYPES = true;
    private static final boolean DEFAULT_STRING_EQUALITY_CHECK = true;
    private static final boolean DEFAULT_AUTO_CONVERT = true;
    
    // Properties loaded directly from file
    private static Properties directProperties;
    
    // Lazy-initialized dependencies
    private DatabaseTypeResolver databaseTypeResolver;
    private IdentifierProperties identifierProperties;
    
    @Value("${identifier.use.native.types:true}")
    private boolean useNativeTypes = true;
    
    @Value("${hibernate.dialect:}")
    private String hibernateDialect;

    /**
     * No-args constructor required by Hibernate for direct instantiation.
     * Dependencies will be lazily loaded from properties files.
     */
    public IdentifierType() {
        logger.fine("IdentifierType instantiated via no-args constructor");
        // Ensure properties are loaded
        loadPropertiesIfNeeded();
    }

    /**
     * Spring-managed constructor with injected dependencies.
     */
    @Autowired
    public IdentifierType(DatabaseTypeResolver databaseTypeResolver, IdentifierProperties identifierProperties) {
        logger.fine("IdentifierType instantiated via Spring DI constructor");
        this.databaseTypeResolver = Objects.requireNonNull(databaseTypeResolver, "DatabaseTypeResolver must not be null");
        this.identifierProperties = Objects.requireNonNull(identifierProperties, "IdentifierProperties must not be null");
        
        // Ensure properties are loaded for static access
        loadPropertiesIfNeeded();
    }
    
    /**
     * Initialize dependencies after construction.
     */
    @PostConstruct
    public void init() {
        logger.fine("IdentifierType @PostConstruct initialization");
        // Ensure dependencies are initialized
        getDatabaseTypeResolver();
        getIdentifierProperties();
    }
    
    /**
     * Loads properties from various configuration files if they haven't been loaded already.
     * This method attempts to load properties from multiple locations to ensure
     * they are available in different deployment scenarios.
     */
    private static synchronized void loadPropertiesIfNeeded() {
        if (directProperties != null) {
            return; // Already loaded
        }
        
        directProperties = new Properties();
        boolean loaded = false;
        
        // Try all configuration locations
        for (String configLocation : CONFIG_LOCATIONS) {
            try {
                // Replace profile placeholder if present
                String location = configLocation;
                if (location.contains("${spring.profiles.active}")) {
                    String profile = System.getProperty("spring.profiles.active", "");
                    if (profile.isEmpty()) {
                        profile = System.getenv("SPRING_PROFILES_ACTIVE");
                        if (profile == null) profile = "";
                    }
                    if (!profile.isEmpty()) {
                        location = location.replace("${spring.profiles.active}", profile);
                    } else {
                        continue; // Skip this location if no profile is active
                    }
                }
                
                Resource resource = null;
                
                // Try different ways to load the resource
                try {
                    // First try as classpath resource
                    if (location.startsWith("classpath:")) {
                        String path = location.substring("classpath:".length());
                        resource = new ClassPathResource(path);
                    }
                    // Then try as file system resource
                    else if (location.startsWith("file:")) {
                        String path = location.substring("file:".length());
                        resource = new FileSystemResource(path);
                    }
                    
                    // If resource is not found or not readable, try using ResourceUtils
                    if (resource == null || !resource.exists()) {
                        URL url = ResourceUtils.getURL(location);
                        if (url != null) {
                            resource = new FileSystemResource(new File(url.getPath()));
                        }
                    }
                    
                    // If still not found, try a direct classpath lookup
                    if (resource == null || !resource.exists()) {
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        if (classLoader == null) {
                            classLoader = IdentifierType.class.getClassLoader();
                        }
                        
                        String simplePath = location;
                        if (simplePath.startsWith("classpath:")) {
                            simplePath = simplePath.substring("classpath:".length());
                        } else if (simplePath.startsWith("file:")) {
                            simplePath = simplePath.substring("file:".length());
                        }
                        
                        InputStream is = classLoader.getResourceAsStream(simplePath);
                        if (is != null) {
                            Properties props = new Properties();
                            props.load(is);
                            is.close();
                            directProperties.putAll(props);
                            loaded = true;
                            logger.fine("Loaded properties from " + location + " using classloader");
                        }
                    }
                    
                    // If resource is found and readable, load it
                    if (resource != null && resource.exists()) {
                        Properties props = PropertiesLoaderUtils.loadProperties(resource);
                        directProperties.putAll(props);
                        loaded = true;
                        logger.fine("Loaded properties from " + location);
                    }
                } catch (Exception e) {
                    logger.log(Level.FINE, "Could not load properties from " + location, e);
                }
            } catch (Exception e) {
                logger.log(Level.FINE, "Error loading properties from " + configLocation, e);
            }
        }
        
        if (!loaded) {
            logger.warning("Could not load any properties files. Using default values.");
        }
    }
    
    /**
     * Gets a property value from the directly loaded properties.
     * 
     * @param key the property key
     * @param defaultValue the default value if the property is not found
     * @return the property value or the default value
     */
    private static String getDirectProperty(String key, String defaultValue) {
        if (directProperties == null) {
            loadPropertiesIfNeeded();
        }
        return directProperties.getProperty(key, defaultValue);
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
            
            // Load values from the directly loaded properties
            String defaultType = getDirectProperty("identifier.type", DEFAULT_ID_TYPE);
            boolean stringEqualityCheck = Boolean.parseBoolean(
                    getDirectProperty("identifier.string.equality.check", String.valueOf(DEFAULT_STRING_EQUALITY_CHECK)));
            boolean autoConvert = Boolean.parseBoolean(
                    getDirectProperty("identifier.auto.convert.string.to.long", String.valueOf(DEFAULT_AUTO_CONVERT)));
            
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
        // try to get it from directly loaded properties
        if (this.useNativeTypes == DEFAULT_USE_NATIVE_TYPES) {
            return Boolean.parseBoolean(
                    getDirectProperty("identifier.use.native.types", String.valueOf(DEFAULT_USE_NATIVE_TYPES)));
        }
        return this.useNativeTypes;
    }
    
    /**
     * Gets the Hibernate dialect.
     * Tries to use the value injected by Spring, otherwise falls back to direct properties or environment.
     */
    private String getDialect() {
        // If Spring has not injected a value, try to get it from directly loaded properties
        if (this.hibernateDialect == null || this.hibernateDialect.isEmpty()) {
            // Try properties files
            String dialect = getDirectProperty("hibernate.dialect", "");
            
            // Try system properties
            if (dialect.isEmpty()) {
                dialect = System.getProperty("hibernate.dialect", "");
            }
            
            // Try environment variables
            if (dialect.isEmpty()) {
                dialect = System.getenv("HIBERNATE_DIALECT");
            }
            
            return dialect;
        }
        return this.hibernateDialect;
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
     * Get the column definition based on the configured ID type and dialect.
     * This can be used in @Column annotations.
     */
    public String getColumnDefinition() {
        String dialect = getDialect();
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
} 
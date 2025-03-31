package com.example.idtypedemo.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for loading application configuration from various sources.
 * Attempts to load properties from multiple locations to ensure configuration
 * is available in different deployment scenarios (Spring context, standalone JAR, etc).
 */
public class ConfigurationLoader {
    
    private static final Logger logger = Logger.getLogger(ConfigurationLoader.class.getName());
    
    // Configuration file paths to try
    private static final String[] CONFIG_LOCATIONS = {
            "classpath:application.properties",
            "classpath:application.yml",
            "classpath:application-${spring.profiles.active}.properties",
            "file:./application.properties",
            "file:./config/application.properties"
    };
    
    // Singleton properties instance
    private static Properties properties;
    
    /**
     * Get a property value from the loaded configuration.
     * 
     * @param key the property key
     * @param defaultValue the default value if the property is not found
     * @return the property value or the default value
     */
    public static String getProperty(String key, String defaultValue) {
        ensurePropertiesLoaded();
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get a boolean property value from the loaded configuration.
     * 
     * @param key the property key
     * @param defaultValue the default value if the property is not found
     * @return the boolean property value or the default value
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Ensure that properties are loaded by calling the load method if needed.
     */
    private static void ensurePropertiesLoaded() {
        if (properties == null) {
            loadProperties();
        }
    }
    
    /**
     * Loads properties from various configuration files.
     * This method attempts to load properties from multiple locations to ensure
     * they are available in different deployment scenarios.
     */
    private static synchronized void loadProperties() {
        if (properties != null) {
            return; // Already loaded
        }
        
        properties = new Properties();
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
                            classLoader = ConfigurationLoader.class.getClassLoader();
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
                            properties.putAll(props);
                            loaded = true;
                            logger.fine("Loaded properties from " + location + " using classloader");
                        }
                    }
                    
                    // If resource is found and readable, load it
                    if (resource != null && resource.exists()) {
                        Properties props = PropertiesLoaderUtils.loadProperties(resource);
                        properties.putAll(props);
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
     * Reloads all properties, discarding any previously loaded values.
     * This is useful for testing or when configuration has changed.
     */
    public static synchronized void reload() {
        properties = null;
        ensurePropertiesLoaded();
    }
} 
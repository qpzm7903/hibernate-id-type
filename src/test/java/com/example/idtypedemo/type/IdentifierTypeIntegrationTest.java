package com.example.idtypedemo.type;

import com.example.idtypedemo.config.IdentifierProperties;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.entity.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("h2")
class IdentifierTypeIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IdentifierProperties identifierProperties;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public IdentifierProperties identifierProperties() {
            IdentifierProperties props = new IdentifierProperties();
            props.setDefaultType("LONG"); // Set to LONG for tests
            return props;
        }
    }

    @Test
    void whenConfiguredForLongType_thenShouldUseBigintColumn() throws SQLException {
        // Set type to LONG
        identifierProperties.setDefaultType("LONG");
        
        // Create and save an entity
        Person person = new Person();
        person.setId(Identifier.of(1L));
        person.setName("John Doe");
        person.setEmail("john@example.com");
        entityManager.persist(person);
        entityManager.flush();
        
        // Check the actual database column type
        String columnType = getColumnType("PERSON", "ID");
        
        // For H2, BIGINT columns are reported as "BIGINT"
        assertTrue(columnType.contains("BIGINT"), 
            "Column type should be BIGINT for LONG ID type, but was: " + columnType);
        
        // Verify we can retrieve the entity
        Person retrieved = entityManager.find(Person.class, Identifier.of(1L));
        assertNotNull(retrieved);
        assertEquals("John Doe", retrieved.getName());
        assertTrue(retrieved.getId().isLong());
        assertEquals(1L, retrieved.getId().asLong());
    }
    
    @Test
    void whenConfiguredForStringType_thenShouldUseVarcharColumn() throws SQLException {
        // Set type to STRING
        identifierProperties.setDefaultType("STRING");
        
        // Create and save an entity
        Person person = new Person();
        person.setId(Identifier.of("abc123"));
        person.setName("Jane Doe");
        person.setEmail("jane@example.com");
        entityManager.persist(person);
        entityManager.flush();
        
        // Check the actual database column type
        String columnType = getColumnType("PERSON", "ID");
        
        // For H2, VARCHAR columns are reported as "VARCHAR"
        assertTrue(columnType.contains("VARCHAR"), 
            "Column type should be VARCHAR for STRING ID type, but was: " + columnType);
        
        // Verify we can retrieve the entity
        Person retrieved = entityManager.find(Person.class, Identifier.of("abc123"));
        assertNotNull(retrieved);
        assertEquals("Jane Doe", retrieved.getName());
        assertTrue(retrieved.getId().isString());
        assertEquals("abc123", retrieved.getId().asString());
    }
    
    /**
     * Helper method to get the column type from the database metadata
     */
    private String getColumnType(String tableName, String columnName) throws SQLException {
        Connection connection = entityManager.unwrap(Connection.class);
        DatabaseMetaData metaData = connection.getMetaData();
        
        try (ResultSet rs = metaData.getColumns(
                connection.getCatalog(), connection.getSchema(), tableName, columnName)) {
            if (rs.next()) {
                return rs.getString("TYPE_NAME");
            }
            throw new IllegalStateException("Column " + columnName + " not found in table " + tableName);
        }
    }
} 
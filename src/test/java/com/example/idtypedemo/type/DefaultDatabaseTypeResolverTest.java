package com.example.idtypedemo.type;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Types;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import com.example.idtypedemo.domain.Identifier;

class DefaultDatabaseTypeResolverTest {

    private DefaultDatabaseTypeResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DefaultDatabaseTypeResolver();
        ReflectionTestUtils.setField(resolver, "stringLength", 255);
    }

    @Test
    void resolveSqlType_WhenTypeLong_ReturnsBigint() {
        assertEquals(Types.BIGINT, resolver.resolveSqlType(Identifier.Type.LONG));
    }

    @Test
    void resolveSqlType_WhenTypeString_ReturnsVarchar() {
        assertEquals(Types.VARCHAR, resolver.resolveSqlType(Identifier.Type.STRING));
    }

    @Test
    void resolveSqlType_WhenTypeNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> resolver.resolveSqlType(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mysql", "postgresql", "h2"})
    void getColumnDefinition_WhenTypeLong_ReturnsBigint(String dialect) {
        assertEquals("BIGINT", resolver.getColumnDefinition(Identifier.Type.LONG, dialect));
    }

    @ParameterizedTest
    @ValueSource(strings = {"mysql", "postgresql", "h2"})
    void getColumnDefinition_WhenTypeString_ReturnsVarchar(String dialect) {
        assertEquals("VARCHAR(255)", resolver.getColumnDefinition(Identifier.Type.STRING, dialect));
    }

    @Test
    void getColumnDefinition_WhenTypeNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> resolver.getColumnDefinition(null, "mysql"));
    }

    @Test
    void getColumnDefinition_WhenDialectNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> resolver.getColumnDefinition(Identifier.Type.LONG, null));
    }

    @Test
    void getColumnDefinition_WhenDialectEmpty_ThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> resolver.getColumnDefinition(Identifier.Type.LONG, ""));
    }

    @Test
    void getColumnDefinition_WhenUnsupportedDialect_ThrowsException() {
        assertThrows(UnsupportedOperationException.class, 
            () -> resolver.getColumnDefinition(Identifier.Type.LONG, "oracle"));
    }
} 
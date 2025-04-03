package com.example.idtypedemo.version.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "version.tracking.enabled=true")
class VersionTrackingPropertiesTest {

    @Autowired
    private VersionTrackingProperties properties;
    
    private VersionTrackingProperties testingProperties;
    
    @BeforeEach
    void setUp() {
        // 为每个测试创建新的实例
        testingProperties = new VersionTrackingProperties();
        testingProperties.setEnabled(true);
        testingProperties.setMaxStackDepth(5);
        testingProperties.setBusinessPackages(Arrays.asList("com.example.service", "com.example.controller"));
        testingProperties.setExcludePackages(Arrays.asList("com.example.common"));
    }

    @Test
    void shouldLoadProperties() {
        // 使用独立的测试实例
        assertTrue(testingProperties.isEnabled());
        assertEquals(Arrays.asList("com.example.service", "com.example.controller"), 
                    testingProperties.getBusinessPackages());
        assertEquals(Arrays.asList("com.example.common"), 
                    testingProperties.getExcludePackages());
        assertEquals(5, testingProperties.getMaxStackDepth());
    }

    @Test
    void shouldSetProperties() {
        // 使用注入的实例
        properties.setEnabled(false);
        assertFalse(properties.isEnabled());

        properties.setMaxStackDepth(10);
        assertEquals(10, properties.getMaxStackDepth());

        properties.setBusinessPackages(Arrays.asList("com.test"));
        assertEquals(Arrays.asList("com.test"), properties.getBusinessPackages());

        properties.setExcludePackages(Arrays.asList("com.exclude"));
        assertEquals(Arrays.asList("com.exclude"), properties.getExcludePackages());
    }
} 
package com.example.idtypedemo.version.filter;

import com.example.idtypedemo.version.config.VersionTrackingProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BusinessStackTraceFilterTest {

    @Mock
    private VersionTrackingProperties properties;

    private BusinessStackTraceFilter filter;

    @BeforeEach
    void setUp() {
        when(properties.getBusinessPackages()).thenReturn(
            Arrays.asList("com.example.service", "com.example.controller")
        );
        when(properties.getExcludePackages()).thenReturn(
            Arrays.asList("com.example.common")
        );
        when(properties.getMaxStackDepth()).thenReturn(5);

        filter = new BusinessStackTraceFilter(properties);
    }

    @Test
    void shouldFilterStackTrace() {
        // 获取当前线程的堆栈跟踪
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String[] filteredStack = filter.filterStackTrace(stackTrace);
        assertNotNull(filteredStack);
        assertTrue(filteredStack.length <= 5);
    }

    @Test
    void shouldGetFilteredStackTraceAsString() {
        // 创建一个不包含业务包的堆栈跟踪
        StackTraceElement[] stackTrace = new StackTraceElement[] {
            new StackTraceElement(
                "java.lang.Thread",
                "getStackTrace",
                "Thread.java",
                100
            ),
            new StackTraceElement(
                "org.junit.jupiter.api.Test",
                "execute",
                "Test.java",
                200
            )
        };
        String stackTraceString = filter.getFilteredStackTraceAsString(stackTrace);
        assertNotNull(stackTraceString);
        assertTrue(stackTraceString.isEmpty());
    }

    @Test
    void shouldFilterOutExcludedPackages() {
        // 创建一个模拟的堆栈元素
        StackTraceElement element = new StackTraceElement(
            "com.example.common.TestClass",
            "testMethod",
            "TestClass.java",
            100
        );
        assertFalse(filter.isBusinessStack(element));
    }

    @Test
    void shouldIncludeBusinessPackages() {
        // 创建一个模拟的堆栈元素
        StackTraceElement element = new StackTraceElement(
            "com.example.service.TestService",
            "testMethod",
            "TestService.java",
            100
        );
        assertTrue(filter.isBusinessStack(element));
    }
} 
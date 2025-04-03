package com.example.idtypedemo.version.aspect;

import com.example.idtypedemo.version.collector.VersionInfoCollector;
import com.example.idtypedemo.version.config.VersionTrackingProperties;
import com.example.idtypedemo.version.filter.BusinessStackTraceFilter;
import jakarta.persistence.Version;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VersionTrackingAspectTest {

    @Mock
    private VersionInfoCollector versionInfoCollector;

    @Mock
    private BusinessStackTraceFilter stackTraceFilter;

    @Mock
    private VersionTrackingProperties properties;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private VersionTrackingAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new VersionTrackingAspect(versionInfoCollector, stackTraceFilter, properties);
    }

    @Test
    void shouldSkipTrackingWhenDisabled() throws Throwable {
        when(properties.isEnabled()).thenReturn(false);
        Object expectedResult = new Object();
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = aspect.trackVersion(joinPoint);

        assertEquals(expectedResult, result);
        verify(versionInfoCollector, never()).getVersionValue(any());
    }

    @Test
    void shouldSkipTrackingWhenEntityIsNull() throws Throwable {
        when(properties.isEnabled()).thenReturn(true);
        when(joinPoint.getArgs()).thenReturn(new Object[]{null});
        Object expectedResult = new Object();
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = aspect.trackVersion(joinPoint);

        assertEquals(expectedResult, result);
        verify(versionInfoCollector, never()).getVersionValue(any());
    }

    @Test
    void shouldTrackVersionChange() throws Throwable {
        // 准备测试实体
        TestEntity entity = new TestEntity();
        when(properties.isEnabled()).thenReturn(true);
        when(joinPoint.getArgs()).thenReturn(new Object[]{entity});
        when(versionInfoCollector.hasVersionField(TestEntity.class)).thenReturn(true);
        when(versionInfoCollector.getVersionValue(entity)).thenReturn(1L, 2L);
        when(stackTraceFilter.getFilteredStackTraceAsString()).thenReturn("stack trace");
        when(joinPoint.proceed()).thenReturn(entity);

        Object result = aspect.trackVersion(joinPoint);

        assertEquals(entity, result);
        verify(versionInfoCollector, times(2)).getVersionValue(entity);
        verify(stackTraceFilter).getFilteredStackTraceAsString();
    }

    @Test
    void shouldNotTrackWhenVersionUnchanged() throws Throwable {
        TestEntity entity = new TestEntity();
        when(properties.isEnabled()).thenReturn(true);
        when(joinPoint.getArgs()).thenReturn(new Object[]{entity});
        when(versionInfoCollector.hasVersionField(TestEntity.class)).thenReturn(true);
        when(versionInfoCollector.getVersionValue(entity)).thenReturn(1L, 1L);
        when(joinPoint.proceed()).thenReturn(entity);

        Object result = aspect.trackVersion(joinPoint);

        assertEquals(entity, result);
        verify(versionInfoCollector, times(2)).getVersionValue(entity);
        verify(stackTraceFilter, never()).getFilteredStackTraceAsString();
    }

    private static class TestEntity {
        @Version
        private Long version;
    }
} 
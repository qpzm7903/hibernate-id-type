package com.example.idtypedemo.version.collector;

import jakarta.persistence.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionInfoCollectorTest {

    private VersionInfoCollector collector;

    @BeforeEach
    void setUp() {
        collector = new VersionInfoCollector();
    }

    @Test
    void shouldReturnNullForNullEntity() {
        assertNull(collector.getVersionValue(null));
    }

    @Test
    void shouldReturnNullForEntityWithoutVersionField() {
        class TestEntity {
            private Long id;
        }
        assertNull(collector.getVersionValue(new TestEntity()));
    }

    @Test
    void shouldReturnVersionValueForEntityWithVersionField() {
        class TestEntity {
            @Version
            private Long version = 1L;
        }
        assertEquals(1L, collector.getVersionValue(new TestEntity()));
    }

    @Test
    void shouldDetectVersionField() {
        class TestEntity {
            @Version
            private Long version;
        }
        assertTrue(collector.hasVersionField(TestEntity.class));
    }

    @Test
    void shouldNotDetectVersionFieldWhenNotPresent() {
        class TestEntity {
            private Long version;
        }
        assertFalse(collector.hasVersionField(TestEntity.class));
    }

    @Test
    void shouldCacheVersionField() {
        class TestEntity {
            @Version
            private Long version;
        }
        // 调用两次以测试缓存
        assertTrue(collector.hasVersionField(TestEntity.class));
        assertTrue(collector.hasVersionField(TestEntity.class));
    }
} 
package com.example.idtypedemo.version.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.persistence.Version;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VersionInfoCollector {
    private static final Logger log = LoggerFactory.getLogger(VersionInfoCollector.class);
    private final ConcurrentHashMap<Class<?>, Field> versionFieldCache = new ConcurrentHashMap<>();

    public Object getVersionValue(Object entity) {
        if (entity == null) {
            return null;
        }

        Field versionField = getVersionField(entity.getClass());
        if (versionField != null) {
            try {
                return versionField.get(entity);
            } catch (IllegalAccessException e) {
                log.warn("Failed to get version value for entity: {}", entity.getClass().getName(), e);
            }
        }
        return null;
    }

    private Field getVersionField(Class<?> entityClass) {
        return versionFieldCache.computeIfAbsent(entityClass, this::findVersionField);
    }

    private Field findVersionField(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Version.class))
                .findFirst()
                .map(field -> {
                    field.setAccessible(true);
                    return field;
                })
                .orElse(null);
    }

    public boolean hasVersionField(Class<?> entityClass) {
        return getVersionField(entityClass) != null;
    }
} 
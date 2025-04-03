package com.example.idtypedemo.version.filter;

import com.example.idtypedemo.version.config.VersionTrackingProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class BusinessStackTraceFilter {
    private final VersionTrackingProperties properties;

    public BusinessStackTraceFilter(VersionTrackingProperties properties) {
        this.properties = properties;
    }

    public String[] filterStackTrace() {
        return filterStackTrace(Thread.currentThread().getStackTrace());
    }

    public String[] filterStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .filter(this::isBusinessStack)
                .limit(properties.getMaxStackDepth())
                .map(StackTraceElement::toString)
                .toArray(String[]::new);
    }

    protected boolean isBusinessStack(StackTraceElement element) {
        String className = element.getClassName();
        return properties.getBusinessPackages().stream()
                .anyMatch(className::startsWith) &&
                properties.getExcludePackages().stream()
                .noneMatch(className::startsWith);
    }

    public String getFilteredStackTraceAsString() {
        return getFilteredStackTraceAsString(Thread.currentThread().getStackTrace());
    }

    public String getFilteredStackTraceAsString(StackTraceElement[] stackTrace) {
        return String.join("\n", filterStackTrace(stackTrace));
    }
} 
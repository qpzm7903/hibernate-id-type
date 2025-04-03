package com.example.idtypedemo.version.aspect;

import com.example.idtypedemo.version.collector.VersionInfoCollector;
import com.example.idtypedemo.version.config.VersionTrackingProperties;
import com.example.idtypedemo.version.filter.BusinessStackTraceFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@ConditionalOnProperty(name = "version.tracking.enabled", havingValue = "true", matchIfMissing = true)
public class VersionTrackingAspect {
    private static final Logger log = LoggerFactory.getLogger(VersionTrackingAspect.class);
    private final VersionInfoCollector versionInfoCollector;
    private final BusinessStackTraceFilter stackTraceFilter;
    private final VersionTrackingProperties properties;

    public VersionTrackingAspect(
            VersionInfoCollector versionInfoCollector,
            BusinessStackTraceFilter stackTraceFilter,
            VersionTrackingProperties properties) {
        this.versionInfoCollector = versionInfoCollector;
        this.stackTraceFilter = stackTraceFilter;
        this.properties = properties;
    }

    @Around("execution(* jakarta.persistence.EntityManager.merge(..)) || " +
            "execution(* jakarta.persistence.EntityManager.persist(..)) || " +
            "execution(* jakarta.persistence.EntityManager.refresh(..))")
    public Object trackVersion(ProceedingJoinPoint pjp) throws Throwable {
        if (!properties.isEnabled()) {
            return pjp.proceed();
        }

        Object entity = pjp.getArgs()[0];
        if (entity == null || !versionInfoCollector.hasVersionField(entity.getClass())) {
            return pjp.proceed();
        }

        Object oldVersion = versionInfoCollector.getVersionValue(entity);
        Object result = pjp.proceed();
        Object newVersion = versionInfoCollector.getVersionValue(entity);

        if (!Objects.equals(oldVersion, newVersion)) {
            logVersionChange(entity, oldVersion, newVersion);
        }

        return result;
    }

    private void logVersionChange(Object entity, Object oldVersion, Object newVersion) {
        if (log.isDebugEnabled()) {
            String stackTrace = stackTraceFilter.getFilteredStackTraceAsString();
            log.debug("""
                Entity: {}
                ID: {}
                Version changed: {} -> {}
                Business stack:
                {}""",
                entity.getClass().getSimpleName(),
                entity.toString(),
                oldVersion,
                newVersion,
                stackTrace);
        }
    }
} 
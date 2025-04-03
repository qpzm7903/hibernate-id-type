package com.example.idtypedemo.version.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要进行版本跟踪的实体类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionTracking {
    /**
     * 是否启用堆栈跟踪
     */
    boolean enableStackTrace() default true;

    /**
     * 堆栈跟踪的深度限制
     */
    int stackTraceDepth() default 10;
} 
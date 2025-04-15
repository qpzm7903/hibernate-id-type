package com.example.idtypedemo.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.fastjson.IdentifierFastjsonDeserializer;
import com.example.idtypedemo.fastjson.IdentifierFastjsonSerializer;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Fastjson配置类，用于注册自定义序列化和反序列化器。
 */
@Configuration
public class FastjsonConfig {

    @PostConstruct
    public void init() {
        // 注册到全局实例，确保全局生效
        SerializeConfig.getGlobalInstance().put(Identifier.class, new IdentifierFastjsonSerializer());
        ParserConfig.getGlobalInstance().putDeserializer(Identifier.class, new IdentifierFastjsonDeserializer());
    }
} 
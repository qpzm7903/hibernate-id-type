package com.example.idtypedemo.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.fastjson.IdentifierFastjsonDeserializer;
import com.example.idtypedemo.fastjson.IdentifierFastjsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fastjson序列化和反序列化的配置类。
 */
@Configuration
public class FastjsonConfig {
    
    /**
     * 为Identifier类配置Fastjson序列化。
     * 这确保使用Fastjson时Identifier对象能被正确序列化。
     */
    @Bean
    public SerializeConfig fastjsonSerializeConfig() {
        SerializeConfig config = SerializeConfig.getGlobalInstance();
        config.put(Identifier.class, new IdentifierFastjsonSerializer());
        return config;
    }
    
    /**
     * 为Identifier类配置Fastjson反序列化。
     * 这确保使用Fastjson时JSON能被正确反序列化为Identifier对象。
     */
    @Bean
    public ParserConfig fastjsonParserConfig() {
        ParserConfig config = ParserConfig.getGlobalInstance();
        config.putDeserializer(Identifier.class, new IdentifierFastjsonDeserializer());
        return config;
    }
} 
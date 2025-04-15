package com.example.idtypedemo.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.example.idtypedemo.domain.Identifier;

import java.lang.reflect.Type;

/**
 * Identifier类的自定义Fastjson反序列化器。
 * 将JSON字符串反序列化为Identifier对象。
 */
public class IdentifierFastjsonDeserializer implements ObjectDeserializer {
    
    @Override
    public Identifier deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        // 处理null值
        if (parser.lexer.token() == JSONToken.NULL) {
            parser.lexer.nextToken();
            return null;
        }
        
        // 解析字符串值
        String value = parser.parseObject(String.class);
        return createIdentifier(value);
    }
    
    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
    
    private Identifier createIdentifier(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            // 首先尝试解析为Long类型
            Long longValue = Long.parseLong(value);
            return Identifier.of(longValue);
        } catch (NumberFormatException e) {
            // 如果不是有效的Long类型，则使用String类型
            return Identifier.of(value);
        }
    }
} 
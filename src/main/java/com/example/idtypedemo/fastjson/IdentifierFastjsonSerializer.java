package com.example.idtypedemo.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.example.idtypedemo.domain.Identifier;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Identifier类的自定义Fastjson序列化器。
 * 将Identifier序列化为字符串表示形式。
 */
public class IdentifierFastjsonSerializer implements ObjectSerializer {
    
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }
        
        Identifier identifier = (Identifier) object;
        // 将标识符序列化为其字符串表示形式
        serializer.write(identifier.toString());
    }
} 
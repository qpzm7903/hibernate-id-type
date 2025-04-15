package com.example.idtypedemo.fastjson;

import com.alibaba.fastjson.JSON;
import com.example.idtypedemo.config.FastjsonConfig;
import com.example.idtypedemo.domain.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 测试Fastjson全局配置的有效性
 * 这个测试验证不需要显式传入序列化/反序列化配置也能正确处理Identifier
 */
@SpringBootTest
@Import(FastjsonConfig.class)
public class FastjsonGlobalConfigTest {
    
    @BeforeEach
    void setUp() {
        // 不需要显式配置，依赖FastjsonConfig中的全局配置
    }
    
    @Test
    void testGlobalSerializerIsEffective() {
        // 创建Long和String类型的Identifier
        Identifier longId = Identifier.of(123L);
        Identifier stringId = Identifier.of("abc-123");
        
        // 不传入任何配置，直接使用全局配置进行序列化
        String longJson = JSON.toJSONString(longId);
        String stringJson = JSON.toJSONString(stringId);
        
        // 验证序列化结果是否正确
        assertEquals("\"123\"", longJson, "Long类型Identifier应当被正确序列化为字符串形式");
        assertEquals("\"abc-123\"", stringJson, "String类型Identifier应当被正确序列化为字符串形式");
    }
    
    @Test
    void testGlobalDeserializerIsEffective() {
        // 准备JSON字符串
        String longJson = "\"123\"";
        String stringJson = "\"abc-123\"";
        String nullJson = "null";
        
        // 不传入任何配置，直接使用全局配置进行反序列化
        Identifier longId = JSON.parseObject(longJson, Identifier.class);
        Identifier stringId = JSON.parseObject(stringJson, Identifier.class);
        Identifier nullId = JSON.parseObject(nullJson, Identifier.class);
        
        // 验证反序列化结果是否正确
        assertEquals(Identifier.of(123L), longId, "字符串形式的数字应被解析为Long类型Identifier");
        assertEquals(Identifier.Type.LONG, longId.getType(), "应当正确识别为Long类型");
        
        assertEquals(Identifier.of("abc-123"), stringId, "非数字字符串应被解析为String类型Identifier");
        assertEquals(Identifier.Type.STRING, stringId.getType(), "应当正确识别为String类型");
        
        assertNull(nullId, "null应当被解析为null");
    }
    
    /**
     * 用于测试复杂对象
     */
    static class TestEntity {
        private Identifier id;
        private String name;
        
        public TestEntity() {}
        
        public TestEntity(Identifier id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Identifier getId() {
            return id;
        }
        
        public void setId(Identifier id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    @Test
    void testComplexObjectSerialization() {
        // 创建测试实体
        TestEntity entity = new TestEntity(Identifier.of(123L), "测试全局配置");
        
        // 不传入任何配置，直接使用全局配置进行序列化
        String json = JSON.toJSONString(entity);
        
        // 验证序列化结果是否正确
        assertEquals("{\"id\":\"123\",\"name\":\"测试全局配置\"}", json, "含Identifier字段的复杂对象应当被正确序列化");
        
        // 不传入任何配置，直接使用全局配置进行反序列化
        TestEntity deserialized = JSON.parseObject(json, TestEntity.class);
        
        // 验证反序列化结果是否正确
        assertEquals(entity.getName(), deserialized.getName(), "普通字段应当保持不变");
        assertEquals(entity.getId(), deserialized.getId(), "Identifier字段应当被正确反序列化");
        assertEquals(Identifier.Type.LONG, deserialized.getId().getType(), "应当正确识别为Long类型");
    }
} 
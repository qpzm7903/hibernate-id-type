package com.example.idtypedemo.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.example.idtypedemo.config.FastjsonConfig;
import com.example.idtypedemo.domain.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Identifier的Fastjson序列化/反序列化单元测试
 */
@SpringBootTest
@Import(FastjsonConfig.class)
class IdentifierFastjsonTest {

    private SerializeConfig serializeConfig;
    private ParserConfig parserConfig;

    @BeforeEach
    void setUp() {
        // 创建显式配置用于特定测试
        serializeConfig = new SerializeConfig();
        serializeConfig.put(Identifier.class, new IdentifierFastjsonSerializer());
        
        parserConfig = new ParserConfig();
        parserConfig.putDeserializer(Identifier.class, new IdentifierFastjsonDeserializer());
        
        // 注意：全局配置由FastjsonConfig类在应用启动时自动注册
    }

    @Test
    void testSerializeLongIdentifier() {
        Identifier id = Identifier.of(123L);
        
        String json = JSON.toJSONString(id, serializeConfig);
        
        assertEquals("\"123\"", json);
    }

    @Test
    void testSerializeStringIdentifier() {
        Identifier id = Identifier.of("abc123");
        
        String json = JSON.toJSONString(id, serializeConfig);
        
        assertEquals("\"abc123\"", json);
    }

    @Test
    void testDeserializeLongIdentifier() {
        String json = "\"123\"";
        
        Identifier id = JSON.parseObject(json, Identifier.class, parserConfig);
        
        assertEquals(Identifier.of(123L), id);
        assertEquals(Identifier.Type.LONG, id.getType());
    }

    @Test
    void testDeserializeStringIdentifier() {
        String json = "\"abc123\"";
        
        Identifier id = JSON.parseObject(json, Identifier.class, parserConfig);
        
        assertEquals(Identifier.of("abc123"), id);
        assertEquals(Identifier.Type.STRING, id.getType());
    }

    @Test
    void testDeserializeNullIdentifier() {
        String json = "null";
        
        Identifier id = JSON.parseObject(json, Identifier.class, parserConfig);
        
        assertNull(id);
    }
    
    /**
     * 测试包装类，用于测试复杂对象中的Identifier字段
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
    void testIdentifierInComplexObject() {
        TestEntity entity = new TestEntity(Identifier.of(123L), "Test");
        
        String json = JSON.toJSONString(entity, serializeConfig);
        
        assertEquals("{\"id\":\"123\",\"name\":\"Test\"}", json);
        
        TestEntity deserialized = JSON.parseObject(json, TestEntity.class, parserConfig);
        
        assertEquals(entity.getName(), deserialized.getName());
        assertEquals(entity.getId(), deserialized.getId());
        assertEquals(Identifier.Type.LONG, deserialized.getId().getType());
    }
    
    @Test
    void testConsistencyWithJacksonSerialization() {
        Identifier longId = Identifier.of(123L);
        Identifier stringId = Identifier.of("abc123");
        
        // 创建类似Jackson的对象进行比较
        class JacksonLike {
            private Identifier id;
            public JacksonLike(Identifier id) { this.id = id; }
            public Identifier getId() { return id; }
        }
        
        String longJson = JSON.toJSONString(new JacksonLike(longId), serializeConfig);
        String stringJson = JSON.toJSONString(new JacksonLike(stringId), serializeConfig);
        
        assertEquals("{\"id\":\"123\"}", longJson, "Long ID序列化应符合预期格式");
        assertEquals("{\"id\":\"abc123\"}", stringJson, "String ID序列化应符合预期格式");
    }
    
    // 下面是使用全局配置的测试
    
    @Test
    void testGlobalSerializerForLongIdentifier() {
        Identifier id = Identifier.of(123L);
        
        // 不传入序列化配置，使用全局配置
        String json = JSON.toJSONString(id);
        
        assertEquals("\"123\"", json, "全局配置应正确序列化Long类型Identifier");
    }
    
    @Test
    void testGlobalSerializerForStringIdentifier() {
        Identifier id = Identifier.of("abc123");
        
        // 不传入序列化配置，使用全局配置
        String json = JSON.toJSONString(id);
        
        assertEquals("\"abc123\"", json, "全局配置应正确序列化String类型Identifier");
    }
    
    @Test
    void testGlobalDeserializerForLongIdentifier() {
        String json = "\"123\"";
        
        // 不传入反序列化配置，使用全局配置
        Identifier id = JSON.parseObject(json, Identifier.class);
        
        assertNotNull(id, "全局配置应正确反序列化Long类型Identifier");
        assertEquals(Identifier.of(123L), id);
        assertEquals(Identifier.Type.LONG, id.getType());
    }
    
    @Test
    void testGlobalDeserializerForStringIdentifier() {
        String json = "\"abc123\"";
        
        // 不传入反序列化配置，使用全局配置
        Identifier id = JSON.parseObject(json, Identifier.class);
        
        assertNotNull(id, "全局配置应正确反序列化String类型Identifier");
        assertEquals(Identifier.of("abc123"), id);
        assertEquals(Identifier.Type.STRING, id.getType());
    }
    
    @Test
    void testGlobalConfigForComplexObject() {
        TestEntity entity = new TestEntity(Identifier.of(123L), "全局配置测试");
        
        // 不传入任何配置，使用全局配置
        String json = JSON.toJSONString(entity);
        TestEntity deserialized = JSON.parseObject(json, TestEntity.class);
        
        assertEquals(entity.getName(), deserialized.getName(), "全局配置应正确处理复杂对象中的普通字段");
        assertEquals(entity.getId(), deserialized.getId(), "全局配置应正确处理复杂对象中的Identifier字段");
        assertEquals(Identifier.Type.LONG, deserialized.getId().getType(), "全局配置应正确保留Identifier类型信息");
    }
} 
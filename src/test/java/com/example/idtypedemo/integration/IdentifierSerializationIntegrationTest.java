package com.example.idtypedemo.integration;

import com.alibaba.fastjson.JSON;
import com.example.idtypedemo.config.FastjsonConfig;
import com.example.idtypedemo.domain.Identifier;
import com.example.idtypedemo.jackson.IdentifierJacksonModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Jackson和Fastjson与Identifier的集成测试。
 * 确保两种序列化框架产生兼容的结果。
 */
@SpringBootTest
@Import(FastjsonConfig.class)
public class IdentifierSerializationIntegrationTest {
    
    private ObjectMapper jacksonMapper;
    
    /**
     * 包含Identifier字段的测试实体类
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
    
    @BeforeEach
    void setUp() {
        // 配置Jackson
        jacksonMapper = new ObjectMapper();
        jacksonMapper.registerModule(new IdentifierJacksonModule());
        
        // 不需要显式配置Fastjson，依赖FastjsonConfig中的全局配置
    }
    
    @Test
    void testJacksonAndFastjsonProduceCompatibleOutput() throws Exception {
        // 创建测试实体
        TestEntity longIdEntity = new TestEntity(Identifier.of(123L), "Test Long");
        TestEntity stringIdEntity = new TestEntity(Identifier.of("abc-123"), "Test String");
        
        // 使用Jackson序列化
        String jacksonLongJson = jacksonMapper.writeValueAsString(longIdEntity);
        String jacksonStringJson = jacksonMapper.writeValueAsString(stringIdEntity);
        
        // 使用Fastjson序列化（不需要显式传入配置）
        String fastjsonLongJson = JSON.toJSONString(longIdEntity);
        String fastjsonStringJson = JSON.toJSONString(stringIdEntity);
        
        // 验证序列化格式是否兼容
        assertEquals(jacksonLongJson, fastjsonLongJson, "Long ID在两个库中的序列化应该相同");
        assertEquals(jacksonStringJson, fastjsonStringJson, "String ID在两个库中的序列化应该相同");
        
        // 用Fastjson反序列化Jackson输出（不需要显式传入配置）
        TestEntity crossDeserializedLong = JSON.parseObject(jacksonLongJson, TestEntity.class);
        TestEntity crossDeserializedString = JSON.parseObject(jacksonStringJson, TestEntity.class);
        
        // 验证跨库反序列化正常工作
        assertEquals(longIdEntity.getId(), crossDeserializedLong.getId(), "当Jackson输出被Fastjson读取时，Long ID应该被保留");
        assertEquals(stringIdEntity.getId(), crossDeserializedString.getId(), "当Jackson输出被Fastjson读取时，String ID应该被保留");
        
        // 用Jackson反序列化Fastjson输出
        TestEntity reverseCrossLong = jacksonMapper.readValue(fastjsonLongJson, TestEntity.class);
        TestEntity reverseCrossString = jacksonMapper.readValue(fastjsonStringJson, TestEntity.class);
        
        // 验证反向跨库反序列化正常工作
        assertEquals(longIdEntity.getId(), reverseCrossLong.getId(), "当Fastjson输出被Jackson读取时，Long ID应该被保留");
        assertEquals(stringIdEntity.getId(), reverseCrossString.getId(), "当Fastjson输出被Jackson读取时，String ID应该被保留");
    }
    
    @Test
    void testRoundTripSerialization() throws Exception {
        // 创建不同ID类型的测试实体
        TestEntity longIdEntity = new TestEntity(Identifier.of(123L), "Test Long");
        TestEntity stringIdEntity = new TestEntity(Identifier.of("abc-123"), "Test String");
        
        // Jackson往返测试
        String jacksonLongJson = jacksonMapper.writeValueAsString(longIdEntity);
        TestEntity jacksonLongRoundTrip = jacksonMapper.readValue(jacksonLongJson, TestEntity.class);
        assertEquals(longIdEntity.getId(), jacksonLongRoundTrip.getId(), "在Jackson往返过程中Long ID应该被保留");
        
        String jacksonStringJson = jacksonMapper.writeValueAsString(stringIdEntity);
        TestEntity jacksonStringRoundTrip = jacksonMapper.readValue(jacksonStringJson, TestEntity.class);
        assertEquals(stringIdEntity.getId(), jacksonStringRoundTrip.getId(), "在Jackson往返过程中String ID应该被保留");
        
        // Fastjson往返测试（不需要显式传入配置）
        String fastjsonLongJson = JSON.toJSONString(longIdEntity);
        TestEntity fastjsonLongRoundTrip = JSON.parseObject(fastjsonLongJson, TestEntity.class);
        assertEquals(longIdEntity.getId(), fastjsonLongRoundTrip.getId(), "在Fastjson往返过程中Long ID应该被保留");
        
        String fastjsonStringJson = JSON.toJSONString(stringIdEntity);
        TestEntity fastjsonStringRoundTrip = JSON.parseObject(fastjsonStringJson, TestEntity.class);
        assertEquals(stringIdEntity.getId(), fastjsonStringRoundTrip.getId(), "在Fastjson往返过程中String ID应该被保留");
    }
} 
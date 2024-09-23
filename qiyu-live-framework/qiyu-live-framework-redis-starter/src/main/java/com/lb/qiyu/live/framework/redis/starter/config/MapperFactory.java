package com.lb.qiyu.live.framework.redis.starter.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.cache.support.NullValue;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 创建并初始化ObjectMapper的工厂类
 * 提供了创建ObjectMapper实例的功能，并根据给定的类型名称进行初始化
 */
public class MapperFactory {

    /**
     * 创建一个新的ObjectMapper实例
     *
     * @return 初始化后的ObjectMapper实例
     */
    public static ObjectMapper newInstance() {
        return initMapper(new ObjectMapper(), (String) null);
    }

    /**
     * 初始化ObjectMapper实例，设置默认类型信息和序列化器
     *
     * @param mapper            需要初始化的ObjectMapper实例
     * @param classPropertyTypeName 类型名称属性的名称，可以为null
     * @return 初始化后的ObjectMapper实例
     */
    private static ObjectMapper initMapper(ObjectMapper mapper, String classPropertyTypeName) {
        // 注册自定义的空值序列化器
        mapper.registerModule(new SimpleModule().addSerializer(new MapperNullValueSerializer(classPropertyTypeName)));

        // 根据给定的类型名称属性，启用默认类型信息
        if (StringUtils.hasText(classPropertyTypeName)) {
            mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, classPropertyTypeName);
        } else {
            mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

        // 禁用未知属性导致的反序列化失败
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }

    /**
     * 序列化器类，用于处理NullValue的序列化
     */
    private static class MapperNullValueSerializer extends StdSerializer<NullValue> {
        private static final long serialVersionUID = 1999052150548658808L;
        private final String classIdentifier;

        /**
         * 构造函数，初始化classIdentifier
         *
         * @param classIdentifier 类型标识符，如果为空则默认为"@class"
         */
        MapperNullValueSerializer(String classIdentifier) {
            super(NullValue.class);
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }

        /**
         * 将NullValue对象序列化为JSON
         *
         * @param value  要序列化的NullValue对象
         * @param jgen   JSON生成器
         * @param provider 序列化提供者
         * @throws IOException 如果序列化过程中发生IO错误
         */
        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }
}

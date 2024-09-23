package com.lb.qiyu.live.framework.redis.starter.config;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 自定义的Redis序列化器，继承并扩展了GenericJackson2JsonRedisSerializer。
 * 主要用于优化字符串和字符类型的序列化处理。
 *
 * @author RainSoul
 * @create 2024-09-23
 */
public class IGenericJackson2JsonRedisSerializer extends GenericJackson2JsonRedisSerializer {

    /**
     * 构造函数，使用新创建的Mapper实例初始化序列化器。
     */
    public IGenericJackson2JsonRedisSerializer() {
        super(MapperFactory.newInstance());
    }

    /**
     * 重写serialize方法，以自定义对象序列化为字节码的方式。
     * 对字符串和字符类型直接进行序列化，其他对象则调用父类方法进行序列化。
     *
     * @param source 需要序列化的对象。
     * @return 序列化后的字节码数组。
     * @throws SerializationException 如果序列化过程中发生错误。
     */
    @Override
    public byte[] serialize(Object source) throws SerializationException {

        // 检查source是否为非空的字符串或字符，是的话则直接将其转换为字节码。
        if (source != null && ((source instanceof String) || (source instanceof Character))) {
            return source.toString().getBytes();
        }
        // 对于其他类型对象，调用父类方法进行序列化。
        return super.serialize(source);
    }
}


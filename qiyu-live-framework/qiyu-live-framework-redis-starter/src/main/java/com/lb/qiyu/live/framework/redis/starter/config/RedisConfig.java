package com.lb.qiyu.live.framework.redis.starter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 配置类，用于定义Redis的相关配置
 *
 * @author RainSoul
 * @create 2024-09-23
 */
@Configuration
@ConditionalOnClass(RedisTemplate.class) // 当RedisTemplate类在classpath中时，该配置类才会被加载
public class RedisConfig {

    /**
     * 配置RedisTemplate实例
     *
     * @param redisConnectionFactory Redis连接工厂，用于建立到Redis服务器的连接
     * @return 配置好的RedisTemplate实例，该实例使用了JSON序列化和反序列化方式处理Redis中的数据
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory); // 设置Redis连接工厂
        // IGenericJackson2JsonRedisSerializer valueSerializer = new IGenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer); // 设置字符串类型的键序列化器
        redisTemplate.setValueSerializer(stringRedisSerializer); // 设置字符串类型的值序列化器
        redisTemplate.setHashKeySerializer(stringRedisSerializer); // 设置Hash类型键的序列化器
        redisTemplate.setHashValueSerializer(stringRedisSerializer); // 设置Hash类型值的序列化器
        redisTemplate.afterPropertiesSet(); // 初始化RedisTemplate
        return redisTemplate; // 返回配置好的RedisTemplate实例
    }
}


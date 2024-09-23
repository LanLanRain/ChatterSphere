package com.lb.qiyu.live.framework.redis.starter.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 实现Condition接口，用于在Spring环境中根据条件加载RedisKeyBuilder的类
 *
 * @author RainSoul
 * @create 2024-09-23
 */
public class RedisKeyLoadMatch implements Condition {
    // 日志记录器
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisKeyLoadMatch.class);

    // Redis键的前缀常量
    private static final String PREFIX = "qiyulive";

    /**
     * 判断是否应该在当前环境中加载给定的类
     *
     * @param context       提供访问当前环境信息的方法
     * @param metadata      提供访问类元数据的方法
     * @return 如果条件匹配则返回true，否则返回false
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 从环境属性中获取应用名称
        String appName = context.getEnvironment().getProperty("spring.application.name");
        // 如果应用名称为空，则记录错误信息并返回false
        if (appName == null) {
            LOGGER.error("没有匹配到应用名称，所以无法加载任何RedisKeyBuilder对象");
            return false;
        }
        try {
            // 通过反射获取className字段
            Field classNameField = metadata.getClass().getDeclaredField("className");
            classNameField.setAccessible(true);
            // 获取类名
            String keyBuilderName = (String) classNameField.get(metadata);
            // 将类名按点分割成列表
            List<String> splitList = Arrays.asList(keyBuilderName.split("\\."));
            // 拼接前缀并转换为小写，用于后续匹配
            String classSimplyName = PREFIX + splitList.get(splitList.size() - 1).toLowerCase();
            // 判断拼接后的类名是否包含应用名称（忽略连字符）
            boolean matchStatus = classSimplyName.contains(appName.replaceAll("-", ""));
            // 记录日志，输出类名和匹配状态
            LOGGER.info("keyBuilderClass is {},matchStatus is {}", keyBuilderName, matchStatus);
        } catch (NoSuchFieldException e) {
            // 如果发生NoSuchFieldException异常，则抛出运行时异常
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // 如果发生IllegalAccessException异常，则抛出运行时异常
            throw new RuntimeException(e);
        }
        // 返回true，表示条件匹配
        return true;
    }
}

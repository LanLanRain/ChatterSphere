package com.lb.qiyu.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Value;

/**
 * Redis键构建器类，用于根据应用名生成特定格式的Redis键
 * 通过在类中定义应用名和分割符，提供方法来获取默认的键前缀
 * 主要目的是为了简化Redis键的管理，使其与应用名相关联，便于区分和管理不同应用的缓存数据
 *
 * @author RainSoul
 * @create 2024-09-23
 */
public class RedisKeyBuilder {
    @Value("${spring.application.name}")
    private String applicationName; // 应用名，用作Redis键的前缀
    private static final String SPLIT_ITEM = ":"; // 定义Redis键中的分割符，用于分隔前缀和实际键名

    /**
     * 获取Redis键中的分割符
     *
     * @return Redis键中的分割符，用于分隔前缀和实际键名
     */
    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    /**
     * 获取Redis键的默认前缀
     * 前缀由应用名和分割符组成，用于标识属于当前应用的缓存数据
     *
     * @return Redis键的前缀，格式为"应用名:"
     */
    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }
}


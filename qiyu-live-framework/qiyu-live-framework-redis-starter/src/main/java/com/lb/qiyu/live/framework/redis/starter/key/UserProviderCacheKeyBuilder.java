package com.lb.qiyu.live.framework.redis.starter.key;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * 用户信息提供者缓存键构建器，继承自RedisKeyBuilder
 *
 * @author RainSoul
 * @create 2024-09-23
 */
@Configuration
@ConditionalOnClass(RedisKeyLoadMatch.class)// 表示该配置类依赖于RedisKeyLoadMatch类的存在
public class UserProviderCacheKeyBuilder extends RedisKeyBuilder{
    // 用户信息的键前缀
    private static String USER_INFO_KEY = "userInfo";
    // 用户标签的键前缀
    private static String USER_TAG_KEY = "userTag";
    // 用户标签锁的键前缀，用于并发控制
    private static String USER_TAG_LOCK_KEY = "userTagLock";
    // 用户手机号列表的键前缀
    private static String USER_PHONE_LIST_KEY = "userPhoneList";
    // 用户手机号对象的键前缀
    private static String USER_PHONE_OBJ_KEY = "userPhoneObj";
    // 用户登录令牌的键前缀
    private static String USER_LOGIN_TOKEN_KEY = "userLoginToken";

    /**
     * 构建用户信息的Redis键
     * @param userId 用户ID
     * @return 用户信息的Redis键
     */
    public String buildUserInfoKey(Long userId) {
        // 返回构建的用户信息键
        return super.getPrefix() + USER_INFO_KEY + super.getSplitItem() + userId;
    }

    /**
     * 构建用户标签锁的Redis键
     * @param userId 用户ID
     * @return 用户标签锁的Redis键
     */
    public String buildTagLockKey(Long userId) {
        // 返回构建的用户标签锁键
        return super.getPrefix() + USER_TAG_LOCK_KEY + super.getSplitItem() + userId;
    }

    /**
     * 构建用户标签的Redis键
     * @param userId 用户ID
     * @return 用户标签的Redis键
     */
    public String buildTagKey(Long userId) {
        // 返回构建的用户标签键
        return super.getPrefix() + USER_TAG_KEY + super.getSplitItem() + userId;
    }

    /**
     * 构建用户手机号列表的Redis键
     * @param userId 用户ID
     * @return 用户手机号列表的Redis键
     */
    public String buildUserPhoneListKey(Long userId) {
        // 返回构建的用户手机号列表键
        return super.getPrefix() + USER_PHONE_LIST_KEY + super.getSplitItem() + userId;
    }

    /**
     * 构建用户手机号对象的Redis键
     * @param phone 手机号
     * @return 用户手机号对象的Redis键
     */
    public String buildUserPhoneObjKey(String phone) {
        // 返回构建的用户手机号对象键
        return super.getPrefix() + USER_PHONE_OBJ_KEY + super.getSplitItem() + phone;
    }

    /**
     * 构建用户登录令牌的Redis键
     * @param tokenKey 令牌键
     * @return 用户登录令牌的Redis键
     */
    public String buildUserLoginTokenKey(String tokenKey) {
        // 返回构建的用户登录令牌键
        return super.getPrefix() + USER_LOGIN_TOKEN_KEY + super.getSplitItem() + tokenKey;
    }
}

package com.lb.live.user.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.lb.live.common.interfaces.topics.UserProviderTopicNames;
import com.lb.live.common.interfaces.utils.ConvertBeanUtils;
import com.lb.live.user.constants.CacheAsyncDeleteCode;
import com.lb.live.user.constants.UserTagFieldNameConstants;
import com.lb.live.user.constants.UserTagsEnum;
import com.lb.live.user.dto.UserCacheAsyncDeleteDTO;
import com.lb.live.user.dto.UserTagDTO;
import com.lb.live.user.provider.dao.mapper.IUserTagMapper;
import com.lb.live.user.provider.dao.po.UserTagPO;
import com.lb.live.user.provider.service.IUserTagService;
import com.lb.live.user.utils.TagInfoUtils;
import com.lb.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author RainSoul
 * @create 2024-09-24
 */
@Service
public class IUserTagServiceImpl implements IUserTagService {

    @Resource
    private IUserTagMapper userTagMapper;

    @Resource
    private RedisTemplate<String, UserTagDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;

    @Resource
    private MQProducer mqProducer;

    /**
     * 设置用户标签
     *
     * @param userId       用户ID
     * @param userTagsEnum 用户标签枚举
     * @return 是否设置成功
     */
    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean updateStatus = userTagMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if (updateStatus) {
            deleteUserTagDTOFromRedis(userId);
        }
        String setNxKey = cacheKeyBuilder.buildTagKey(userId);

        // 分布式并发场景下用户标签接口的优化以及初始化问题
        String setNxResult = redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer keySerializer = redisTemplate.getKeySerializer();
            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
            return (String) connection.execute("set", keySerializer.serialize(setNxKey),
                    valueSerializer.serialize("-1"),
                    "NX".getBytes(StandardCharsets.UTF_8),
                    "EX".getBytes(StandardCharsets.UTF_8),
                    "3".getBytes(StandardCharsets.UTF_8));
        });
        if (!"OK".equals(setNxResult)) {
            return false;
        }
        UserTagPO userTagPO = userTagMapper.selectById(userId);
        if (userTagPO != null) {
            return false;
        }
        userTagPO = new UserTagPO();
        userTagPO.setUserId(userId);
        userTagMapper.insert(userTagPO);
        updateStatus = userTagMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        redisTemplate.delete(setNxKey);
        return updateStatus;
    }


    /**
     * 取消用户标签
     *
     * @param userId       用户ID
     * @param userTagsEnum 用户标签枚举
     * @return 是否取消成功
     */
    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean cancelStatus = userTagMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if (!cancelStatus) {
            return false;
        }
        deleteUserTagDTOFromRedis(userId);
        return true;
    }


    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        UserTagDTO userTagDTO = this.queryByUserIdFromRedis(userId);
        UserTagPO userTagPO = userTagMapper.selectById(userId);
        if (userTagPO == null) {
            return false;
        }
        String fieldName = userTagsEnum.getFieldName();
        if (fieldName.equals(UserTagFieldNameConstants.TAG_INFO_01)) {
            return TagInfoUtils.isContainTag(userTagPO.getTagInfo01(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAG_INFO_02)) {
            return TagInfoUtils.isContainTag(userTagPO.getTagInfo02(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAG_INFO_03)) {
            return TagInfoUtils.isContainTag(userTagPO.getTagInfo03(), userTagsEnum.getTag());
        }
        return false;
    }

    /**
     * 从 Redis 中删除用户标签数据并异步通知删除操作。
     * <p>
     * 该方法首先根据用户 ID 构建 Redis 键，然后尝试从 Redis 中删除该键对应的数据。
     * 如果删除成功，它会创建一个包含删除操作信息的异步删除DTO，并将其发送到指定的 MQ 主题。
     * 如果删除失败，它会捕获异常并打印堆栈跟踪。
     *
     * @param userId 要删除标签的用户 ID。
     * @throws Exception 如果在发送消息到 MQ 时发生错误。
     */
    public void deleteUserTagDTOFromRedis(Long userId) {
        String key = cacheKeyBuilder.buildTagKey(userId);
        redisTemplate.delete(key);

        UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
        userCacheAsyncDeleteDTO.setCode(CacheAsyncDeleteCode.USER_TAG_DELETE.getCode());
        Map<String, Object> jsonParam = new HashMap<>();
        jsonParam.put("userId", userId);
        userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));

        // 发送消息到MQ
        Message message = new Message();
        message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);
        message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
        message.setDelayTimeLevel(1);

        try {
            mqProducer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从redis中查询用户标签对象
     *
     * @param userId
     * @return
     */
    private UserTagDTO queryByUserIdFromRedis(Long userId) {
        String redisKey = cacheKeyBuilder.buildTagKey(userId);
        // [5.8] 用户标签引入Redis缓存
        UserTagDTO userTagDTO = redisTemplate.opsForValue().get(redisKey);
        if (userTagDTO != null) {
            return userTagDTO;
        }
        UserTagPO userTagPO = userTagMapper.selectById(userId);
        if (userTagPO == null) {
            return null;
        }
        userTagDTO = ConvertBeanUtils.convert(userTagPO, UserTagDTO.class);
        redisTemplate.opsForValue().set(redisKey, userTagDTO);
        redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
        return userTagDTO;
    }
}

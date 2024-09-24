package com.lb.live.user.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.lb.live.common.interfaces.topics.UserProviderTopicNames;
import com.lb.live.common.interfaces.utils.ConvertBeanUtils;
import com.lb.live.user.dto.UserCacheAsyncDeleteDTO;
import com.lb.live.user.dto.UserDTO;
import com.lb.live.user.provider.dao.mapper.IUserMapper;
import com.lb.live.user.provider.dao.po.UserPO;
import com.lb.live.user.provider.service.IUserService;
import com.lb.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
@Service
public class IUserServiceImpl implements IUserService {

    @Resource
    private MQProducer mqProducer;

    @Resource
    private IUserMapper userMapper;

    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    /**
     * 根据用户ID获取用户DTO信息
     * 该方法首先尝试从Redis缓存中获取用户信息，如果缓存中不存在，则从数据库中获取，并将结果存入缓存
     *
     * @param userId 用户ID，用于查询用户信息
     * @return 用户的DTO对象，如果用户不存在则返回null
     */
    @Override
    public UserDTO getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = (UserDTO) redisTemplate.opsForValue().get(key);
        if (userDTO != null) {
            return userDTO;
        }
        userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if (userDTO != null) {
            redisTemplate.opsForValue().set(key, userDTO);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        int updateStatus = userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        if (updateStatus > -1) {
            String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
            redisTemplate.delete(key);
            UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
            Map<String, Object> jsonParam = new HashMap<>();
            jsonParam.put("userId", userDTO.getUserId());
            userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));
            try {
                Message message = new Message();
                message.setBody(JSON.toJSONString(userDTO).getBytes());
                message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);
                message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
                message.setDelayTimeLevel(1);
                mqProducer.send(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    /**
     * 该方法主要用于插入单个用户数据，通过UserDTO对象接收用户信息，并将其转换为UserPO对象进行数据库插入操作
     *
     * @param userDTO 用户数据传输对象，包含用户信息
     * @return boolean 插入操作的成功与否，true代表成功插入，false代表插入失败或参数无效
     */
    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        int insertStatus = userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        if (insertStatus > -1) {
            return true;
        }
        return false;
    }


    /**
     * 批量查询用户信息
     * 该方法首先尝试从Redis缓存中获取用户信息，如果缓存不命中，则通过数据库查询缺失的用户信息，并将这些信息更新到缓存中
     *
     * @param userIdList 用户ID列表
     * @return 包含用户信息的Map，键为用户ID
     */
    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }

        // 过滤用户ID，只保留大于10000的ID
        userIdList = userIdList.stream().filter(id -> id > 10000).collect(Collectors.toList());
        // 如果过滤后的用户ID列表为空，则直接返回空Map
        if (CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }

        // 初始化key列表，用于Redis操作
        List<String> keyList = new ArrayList<>();

        // 根据用户ID生成对应的Redis键，并添加到key列表中
        userIdList.forEach(userId -> {
            keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
        });

        // 使用Redis的批量获取操作获取用户信息
        List<UserDTO> userDTOList = redisTemplate.opsForValue().multiGet(keyList).stream()
                .filter(x -> x != null).collect(Collectors.toList());

        // 如果全部用户信息都已经在缓存中找到，则直接返回结果
        if (CollectionUtils.isNotEmpty(userDTOList) && userDTOList.size() == userIdList.size()) {
            return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));
        }

        // 分离出缓存中未找到的用户ID
        List<Long> userIdInCacheList = userDTOList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Long> userIdNotInCacheList = userIdList.stream().filter(x -> !userIdInCacheList.contains(x))
                .collect(Collectors.toList());

        // 使用多线程查询数据库，以提高查询效率
        // parallelStream 是 Java 8 引入的一个方法，用于创建并行流（Parallel Stream）。并行流可以自动利用多核处理器的优势，将数据分割成多个部分，并行地处理这些数据。
        // 这样可以显著提高某些操作的执行效率，特别是对于大数据量和计算密集型任务。
        Map<Long, List<Long>> userIdMap = userIdNotInCacheList.stream().collect(Collectors.groupingBy(x -> x % 100));
        List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();
        userIdMap.values().parallelStream().forEach(queryUserIdList -> {
            dbQueryResult.addAll(ConvertBeanUtils.convertList(userMapper.selectBatchIds(queryUserIdList), UserDTO.class));
        });

        // 将数据库查询结果更新到Redis缓存中
        if (CollectionUtils.isNotEmpty(dbQueryResult)) {
            Map<String, UserDTO> saveCacheMap = dbQueryResult.stream()
                    .collect(Collectors.toMap(userDTO -> userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(saveCacheMap);

            // 使用管道线操作减少Redis通信次数，提高效率
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String redisKey : saveCacheMap.keySet()) {
                        operations.expire((K) redisKey, createRandomTime(), TimeUnit.SECONDS);
                    }
                    return null;
                }
            });

            // 合并数据库查询结果到用户信息列表中
            userDTOList.addAll(dbQueryResult);
        }

        // 返回最终的用户信息Map
        return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));
    }

    /**
     * 创建随机的过期时间 用于redis设置key过期
     *
     * @return 随机时间对应的秒数，加上30分钟的基础时间
     */
    private int createRandomTime() {
        // 生成一个0到10000之间的随机数，代表随机的秒数
        int randomNumSecond = ThreadLocalRandom.current().nextInt(10000);
        return randomNumSecond + 30 * 60;
    }
}

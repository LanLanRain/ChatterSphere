package com.lb.live.user.provider.config;

import com.alibaba.fastjson.JSON;
import com.lb.live.user.dto.UserDTO;
import com.lb.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RocketMQ的消费者bean配置类
 *
 * @author RainSoul
 * @create 2024-09-23
 */
@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

    /**
     * InitializingBean 是 Spring 框架中的一个接口，它的作用是在 Bean 完成初始化后执行一些自定义的操作。实现这个接口的类需要提供 afterPropertiesSet() 方法。
     * Spring 容器会调用该方法确保 Bean 的某些属性已经被正确设置。主要用途包括：
     * 进行额外的初始化操作
     * 校验配置属性是否正确设置
     * 资源加载或预处理等操作
     * 通过实现 InitializingBean 接口，可以更好地控制 Bean 的生命周期。
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    @Resource
    private RocketMQConsumerProperties consumerProperties;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        initConsumer();
    }

    /**
     * 初始化RocketMQ消费者
     *
     * 本函数负责创建并启动一个RocketMQ消费者，设置必要的消费者属性，
     * 订阅主题并设置消息监听器，监听到消息后根据消息内容执行相关操作
     */
    public void initConsumer() {
        try {
            // 初始化我们的RocketMQ消费者
            var defaultMQPushConsumer = new DefaultMQPushConsumer();
            // 设置NameServer地址
            defaultMQPushConsumer.setNamesrvAddr(consumerProperties.getNameSrv());
            // 设置消费者组
            defaultMQPushConsumer.setConsumerGroup(consumerProperties.getGroupName());
            // 设置每次消费的消息最大数量为1，即每次只消费一条消息
            defaultMQPushConsumer.setConsumeMessageBatchMaxSize(1);
            // 设置消费位置，从最早的消息开始消费
            defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            // 设置消息监听器，处理消费的消息
            defaultMQPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                // 解析消息内容
                String msgStr = new String(msgs.get(0).getBody());
                UserDTO userDTO = JSON.parseObject((String)JSON.parseObject(msgStr).get("json"), UserDTO.class);
                // 校验消息内容是否有效
                if (userDTO == null || userDTO.getUserId() == null) {
                    LOGGER.error("用户id为空，参数异常，内容:{}", msgStr);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                // 处理消息，根据用户ID删除相关缓存
                redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()));
                LOGGER.info("延迟删除处理成功，userDTO is {}", userDTO);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            // 启动消费者
            defaultMQPushConsumer.start();
            // 记录消费者启动成功日志
            LOGGER.info("mq消费者启动成功,nameSrv is {}", consumerProperties.getNameSrv());
        } catch (MQClientException e) {
            // 捕获RocketMQ客户端异常并抛出运行时异常
            throw new RuntimeException(e);
        }
    }
}

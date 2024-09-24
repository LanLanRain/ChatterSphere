package com.lb.live.user.provider.config;

import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author RainSoul
 * @create 2024-09-23
 */
@Configuration
public class RocketMQProducerConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(RocketMQProducerConfig.class);

    @Resource
    private RocketMQProducerProperties producerProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 创建并启动一个MQ生产者实例
     * 该方法负责初始化线程池、配置生产者并启动它
     *
     * @return DefaultMQProducer 配置并启动后的MQ生产者实例
     */
    @Bean
    public MQProducer mqProducer() {
        /**
         * 创建一个线程池，用于生成特定名称格式的线程，用于异步处理任务。
         *
         * @param applicationName 应用程序名称，用于构建线程名称的前缀
         * @return 返回一个自定义名称格式的线程池
         */
        ThreadPoolExecutor asyncThreadPoolExecutor = new ThreadPoolExecutor(100, 150, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), r -> {
            // 创建一个新线程
            Thread thread = new Thread(r);
            // 使用应用程序名称和随机数来设置线程名称
            thread.setName(applicationName + ":rmq-producer:" + ThreadLocalRandom.current().nextInt(1000));
            return thread;
        });


        //创建一个默认的MQ生产者实例
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        try {
            //设置名称服务器地址
            defaultMQProducer.setNamesrvAddr(producerProperties.getNameSrv());
            //设置生产者组名
            defaultMQProducer.setProducerGroup(producerProperties.getGroupName());
            //设置发送失败时的重试次数
            defaultMQProducer.setRetryTimesWhenSendFailed(producerProperties.getRetryTimes());
            //设置异步发送失败时的重试次数
            defaultMQProducer.setRetryTimesWhenSendAsyncFailed(producerProperties.getRetryTimes());
            //当Broker不返回STORE_OK时，是否尝试再发送到其他Broker
            defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
            //设置异步发送的线程池
            defaultMQProducer.setAsyncSenderExecutor(asyncThreadPoolExecutor);
            //启动生产者
            defaultMQProducer.start();
            //日志记录生产者启动成功
            LOGGER.info("mq生产者启动成功,nameSrv is {}", producerProperties.getNameSrv());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
        //返回配置并启动后的生产者实例
        return defaultMQProducer;
    }
}

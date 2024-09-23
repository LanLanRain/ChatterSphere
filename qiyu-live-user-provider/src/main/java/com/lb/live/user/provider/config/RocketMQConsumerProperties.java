package com.lb.live.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description: rocketmq消费者配置
 * @author RainSoul
 * @create 2024-09-23
 */
@Configuration
@ConfigurationProperties(prefix = "qiyu.rmq.consumer")
public class RocketMQConsumerProperties {

    //rocketmq的nameSever地址
    private String nameSrv;
    //分组名称
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNameSrv() {
        return nameSrv;
    }

    public void setNameSrv(String nameSrv) {
        this.nameSrv = nameSrv;
    }

    @Override
    public String toString() {
        return "RocketMQConsumerProperties{" +
                "groupName='" + groupName + '\'' +
                ", nameSrv='" + nameSrv + '\'' +
                '}';
    }
}

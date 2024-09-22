package com.lb.live.user.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
// 启动Spring Boot应用程序
@SpringBootApplication
// 启用服务发现功能，允许应用程序轻松发现和调用其他服务
@EnableDiscoveryClient
// 启用Dubbo集成，使Spring Boot应用程序能够作为Dubbo服务提供者或消费者
@EnableDubbo
public class UserProviderApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new  SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}

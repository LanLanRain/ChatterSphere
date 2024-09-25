package com.lb.live.user.provider;

import com.lb.live.user.provider.service.IUserTagService;
import jakarta.annotation.Resource;
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
public class UserProviderApplication/*  implements CommandLineRunner  */ {

    @Resource
    private IUserTagService userTagService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }


    /* @Override
    public void run(String... args) throws Exception {
        long userId = 1001L;
        System.out.println(userTagService.setTag(1001L, UserTagsEnum.IS_RICH));
        System.out.println("当前用户是否包含 is_rich 标签 " + userTagService.containTag(1001L, UserTagsEnum.IS_RICH));
        System.out.println(userTagService.setTag(1001L, UserTagsEnum.IS_VIP));
        System.out.println("当前用户是否包含 is_vip 标签" + userTagService.containTag(1001L, UserTagsEnum.IS_VIP));
        System.out.println(userTagService.setTag(1001L, UserTagsEnum.IS_OLD_USER));
        System.out.println("当前用户是否包含 is_old_user 标签" + userTagService.containTag(1001L, UserTagsEnum.IS_OLD_USER));
        System.out.println("---------------------------------------------------------------------");
        System.out.println(userTagService.cancelTag(1001L, UserTagsEnum.IS_OLD_USER));
        System.out.println("当前用户是否包含 is_old_user 标签" + userTagService.containTag(1001L, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.cancelTag(1001L, UserTagsEnum.IS_VIP));
        System.out.println("当前用户是否包含 is_vip 标签" + userTagService.containTag(1001L, UserTagsEnum.IS_VIP));
        System.out.println(userTagService.cancelTag(1001L, UserTagsEnum.IS_RICH));
        System.out.println("当前用户是否包含 is_rich 标签 " + userTagService.containTag(1001L, UserTagsEnum.IS_RICH));
    } */
}

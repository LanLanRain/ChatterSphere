package com.lb.live.id.generate.provider;

import com.lb.live.id.generate.provider.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashSet;

/**
 * @author RainSoul
 * @create 2024-09-24
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class IdGenerateApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IdGenerateApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    IdGenerateService idGenerateService;

    @Override
    public void run(String... args) throws Exception {
        HashSet<Long> idSet = new HashSet<>();
        for (int i = 0; i < 1300; i++) {
            Long id = idGenerateService.getSeqId(1);
            idSet.add(id);
        }
        System.out.println(idSet.size());
    }
}

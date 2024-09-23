package com.lb.qiyu.live.framework.datasource.starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 配置类，用于在启动时初始化数据源的连接
 * 通过Spring Boot的@Configuration标记为配置类
 */
@Configuration
public class ShardingJdbcDatasourceAutoInitConnectionConfig {
    // 日志记录器，用于记录重要的信息和错误
    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingJdbcDatasourceAutoInitConnectionConfig.class);

    /**
     * 在应用启动时执行的数据源连接初始化操作
     * 使用ApplicationRunner接口实现启动后操作
     *
     * @param dataSource 数据源对象，用于获取数据库连接
     * @return ApplicationRunner的实例，定义启动后操作
     */
    @Bean
    public ApplicationRunner applicationRunner(DataSource dataSource) {
        return args -> {
            LOGGER.info(" ==================  [ShardingJdbcDatasourceAutoInitConnectionConfig] dataSource: {}", dataSource);
            //手动触发下连接池的连接创建
            Connection connection = dataSource.getConnection();
        };
    }
}

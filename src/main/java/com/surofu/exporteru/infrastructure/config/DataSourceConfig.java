package com.surofu.exporteru.infrastructure.config;

import com.surofu.exporteru.application.utils.RoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Profile("prod")
@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class DataSourceConfig {

    private final Environment env;

    @Bean
    public DataSource writeDataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(env.getProperty("app.datasource.write.url"));
        dataSource.setUsername(env.getProperty("app.datasource.write.username"));
        dataSource.setPassword(env.getProperty("app.datasource.write.password"));
        dataSource.setDriverClassName(env.getProperty("app.datasource.write.driver-class-name"));
        dataSource.setAutoCommit(Boolean.TRUE.equals(env.getProperty("app.datasource.hikari.auto-commit", Boolean.class)));
        dataSource.setMaximumPoolSize(Objects.requireNonNull(env.getProperty("app.datasource.hikari.maximum-pool-size", Integer.class)));
        dataSource.setMinimumIdle(Objects.requireNonNull(env.getProperty("app.datasource.hikari.minimum-idle", Integer.class)));
        dataSource.setConnectionTimeout(Objects.requireNonNull(env.getProperty("app.datasource.hikari.connection-timeout", Integer.class)));
        dataSource.setIdleTimeout(Objects.requireNonNull(env.getProperty("app.datasource.hikari.idle-timeout", Integer.class)));
        dataSource.setLeakDetectionThreshold(Objects.requireNonNull(env.getProperty("app.datasource.hikari.leak-detection-threshold", Integer.class)));
        return dataSource;
    }

    @Bean
    public DataSource readDataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(env.getProperty("app.datasource.read.url"));
        dataSource.setUsername(env.getProperty("app.datasource.read.username"));
        dataSource.setPassword(env.getProperty("app.datasource.read.password"));
        dataSource.setDriverClassName(env.getProperty("app.datasource.read.driver-class-name"));
        dataSource.setAutoCommit(Boolean.TRUE.equals(env.getProperty("app.datasource.hikari.auto-commit", Boolean.class)));
        dataSource.setMaximumPoolSize(Objects.requireNonNull(env.getProperty("app.datasource.hikari.maximum-pool-size", Integer.class)));
        dataSource.setMinimumIdle(Objects.requireNonNull(env.getProperty("app.datasource.hikari.minimum-idle", Integer.class)));
        dataSource.setConnectionTimeout(Objects.requireNonNull(env.getProperty("app.datasource.hikari.connection-timeout", Integer.class)));
        dataSource.setIdleTimeout(Objects.requireNonNull(env.getProperty("app.datasource.hikari.idle-timeout", Integer.class)));
        dataSource.setLeakDetectionThreshold(Objects.requireNonNull(env.getProperty("app.datasource.hikari.leak-detection-threshold", Integer.class)));

        return dataSource;
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        return routingDataSource();
    }

    @Bean
    public DataSource routingDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("write", writeDataSource());
        targetDataSources.put("read", readDataSource());

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource());

        return routingDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(routingDataSource())
                .packages("com.surofu.exporteru")
                .persistenceUnit("default")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

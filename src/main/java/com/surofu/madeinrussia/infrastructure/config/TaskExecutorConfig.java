package com.surofu.madeinrussia.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {

    @Bean
    public TaskExecutor appTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MadeInRussiaTaskExecutor-");
        executor.initialize();
        return executor;
    }
}

package com.dzytsiuk.excelreportservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;

@Configuration
public class RootConfig {
    @Bean
    public ExecutorService threadPoolTaskExecutor(@Value("${thread.pool.initial}") Integer initialPoolSize,
                                                    @Value("${thread.pool.max}") Integer maxPoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(initialPoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
        return new ExecutorServiceAdapter(executor);
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter(@Value("${request.date.format}") String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}

package com.surofu.madeinrussia.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    @Value("${app.mail-verification.duration-in-minutes}")
    private int durationInMinutes;

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(100)
        );

        return cacheManager;
    }

    @Bean
    public CacheManager verificationCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("unverifiedUsers", "unverifiedUserPasswords", "verificationCodes");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(durationInMinutes, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats()
        );
        return cacheManager;
    }
}

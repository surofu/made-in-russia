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

    @Value("${app.cache.expires-after-write-in-minutes}")
    private int expireAfterWriteInMinutes;

    @Value("${app.cache.expires-after-access-in-minutes}")
    private int expireAfterAccessInMinutes;

    @Value("${app.cache.maximum-size}")
    private int maximumSize;

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWriteInMinutes, TimeUnit.MINUTES)
                .expireAfterAccess(expireAfterAccessInMinutes, TimeUnit.MINUTES)
                .maximumSize(maximumSize)
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

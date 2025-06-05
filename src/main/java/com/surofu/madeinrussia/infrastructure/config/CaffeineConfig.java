package com.surofu.madeinrussia.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.surofu.madeinrussia.application.utils.UserVerificationCaffeineCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class CaffeineConfig {

    private final UserVerificationCaffeineCacheManager userVerificationCaffeineCacheManager;
    @Value("${app.mail-verification.duration-in-minutes}")
    private Integer durationInMinutes;
    @Value("${app.cache.expires-after-write-in-minutes}")
    private Integer expireAfterWriteInMinutes;
    @Value("${app.cache.expires-after-access-in-minutes}")
    private Integer expireAfterAccessInMinutes;
    @Value("${app.cache.maximum-size}")
    private Integer maximumSize;

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
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                userVerificationCaffeineCacheManager.getCacheName()
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(durationInMinutes, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats()
        );
        return cacheManager;
    }
}

package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class CompanyFirstNameCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "COMPANY_FIRST_NAME";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, HstoreTranslationDto> listHashOperations;

    public CompanyFirstNameCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listHashOperations = redisTemplate.opsForHash();
    }

    public HstoreTranslationDto get(String key) {
        return listHashOperations.get(CACHE_NAME, key);
    }

    public void set(String key, HstoreTranslationDto value) {
        listHashOperations.put(CACHE_NAME, key, value);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds() * 10, TimeUnit.SECONDS);
    }

    public boolean contains(String key) {
        return listHashOperations.hasKey(CACHE_NAME, key);
    }
}

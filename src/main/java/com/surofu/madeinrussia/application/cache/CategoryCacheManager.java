package com.surofu.madeinrussia.application.cache;

import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class CategoryCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "CATEGORY";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, CategoryDto> hashOperations;

    public CategoryCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public CategoryDto getCategory(String hash) {
        return hashOperations.get(CACHE_NAME, hash);
    }

    public void setCategory(String hash, CategoryDto category) {
        hashOperations.put(CACHE_NAME, hash, category);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void clearByHash(String hash) {
        hashOperations.delete(CACHE_NAME, hash);
    }

    public boolean contains(String hash) {
        return hashOperations.hasKey(CACHE_NAME, hash);
    }

    public void clearAll() {
        redisTemplate.delete(CACHE_NAME);
    }
}

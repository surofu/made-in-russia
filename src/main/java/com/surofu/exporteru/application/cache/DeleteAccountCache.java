package com.surofu.exporteru.application.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DeleteAccountCache {

    @Value("${app.redis.verification-ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "DELETE_ACCOUNT_CACHE";
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    public DeleteAccountCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public String get(String email) {
        return hashOperations.get(CACHE_NAME, email);
    }

    public void put(String email, String code) {
        hashOperations.put(CACHE_NAME, email, code);
        redisTemplate.expire(CACHE_NAME, ttl);
    }

    public void remove(String email) {
        hashOperations.delete(CACHE_NAME, email);
    }
}

package com.surofu.madeinrussia.application.cache;

import com.surofu.madeinrussia.application.dto.GeneralDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;

@Component
public class GeneralCacheService {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "GENERAL";
    private final String ALL_CACHE_NAME = "ALL";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, GeneralDto> hashOperations;

    public GeneralCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public GeneralDto get(Locale locale) {
        return hashOperations.get(CACHE_NAME, createHash(locale));
    }

    public void set(Locale locale, GeneralDto generalDto) {
        hashOperations.put(CACHE_NAME, createHash(locale), generalDto);
        redisTemplate.expire(CACHE_NAME, ttl);
    }

    public void clear() {
        hashOperations.delete(CACHE_NAME, ALL_CACHE_NAME);
    }

    public boolean exists(Locale locale) {
        return hashOperations.hasKey(CACHE_NAME, createHash(locale));
    }

    private String createHash(Locale locale) {
        return ALL_CACHE_NAME + "_" + locale.getLanguage();
    }
}

package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.seo.SeoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class SeoCacheManager {
    private final String CACHE_NAME = "SEO";
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    public SeoCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public SeoDto get() {
        return (SeoDto) redisTemplate.opsForValue().get(CACHE_NAME);
    }

    public void set(SeoDto seo) {
        redisTemplate.opsForValue().set(CACHE_NAME, seo);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public boolean contains() {
        return redisTemplate.hasKey(CACHE_NAME);
    }
}

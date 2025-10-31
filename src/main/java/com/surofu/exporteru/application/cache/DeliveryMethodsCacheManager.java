package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class DeliveryMethodsCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "DELIVERY_METHODS";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, DeliveryMethodDto> hashOperations;

    public DeliveryMethodsCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public DeliveryMethodDto getDeliveryMethod(String hash) {
        return hashOperations.get(CACHE_NAME, hash);
    }

    public void setDeliveryMethod(String hash, DeliveryMethodDto deliveryMethod) {
        hashOperations.put(CACHE_NAME, hash, deliveryMethod);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public boolean contains(String hash) {
        return hashOperations.hasKey(CACHE_NAME, hash);
    }

    public void clearAll() {
        redisTemplate.delete(CACHE_NAME);
    }
}

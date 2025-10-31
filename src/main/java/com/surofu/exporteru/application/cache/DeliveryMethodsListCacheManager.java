package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.DeliveryMethodDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DeliveryMethodsListCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "DELIVERY_METHOD_LIST";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, List<DeliveryMethodDto>> hashOperations;

    public DeliveryMethodsListCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public List<DeliveryMethodDto> getDeliveryMethod(String hash) {
        return hashOperations.get(CACHE_NAME, hash);
    }

    public void setDeliveryMethod(String hash, List<DeliveryMethodDto> deliveryMethodList) {
        hashOperations.put(CACHE_NAME, hash, deliveryMethodList);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public boolean contains(String hash) {
        return hashOperations.hasKey(CACHE_NAME, hash);
    }

    public void clearAll() {
        redisTemplate.delete(CACHE_NAME);
    }
}

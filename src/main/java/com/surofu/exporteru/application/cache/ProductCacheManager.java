package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.product.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class ProductCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "PRODUCTS";
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, ProductDto> hashOperations;

    public ProductCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public ProductDto getProduct(Long id, String locale) {
        return hashOperations.get(CACHE_NAME, id + locale);
    }

    public void setProduct(Long id, String locale, ProductDto product) {
        hashOperations.put(CACHE_NAME, id + locale, product);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void clearById(Long id, String locale) {
        hashOperations.delete(CACHE_NAME, id + locale);
    }

    public void clearById(Long id) {
        clearById(id, "en");
        clearById(id, "ru");
        clearById(id, "zh");
        clearById(id, "hi");
    }

    public void clearAll() {
        redisTemplate.delete(CACHE_NAME);
    }

    public boolean contains(Long id, String locale) {
        return hashOperations.hasKey(CACHE_NAME, id + locale);
    }
}

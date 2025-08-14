package com.surofu.madeinrussia.application.cache;

import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class ProductSummaryCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "PRODUCT_SUMMARY_PAGE";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Page<ProductSummaryViewDto>> hashOperations;

    public ProductSummaryCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public Page<ProductSummaryViewDto> getFirstPage(String hash) {
        return hashOperations.get(CACHE_NAME, hash);
    }

    public void setFirstPage(String hash, Page<ProductSummaryViewDto> page) {
        hashOperations.put(CACHE_NAME, hash, page);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void clearAll() {
        redisTemplate.delete(CACHE_NAME);
    }

    public boolean contains(String hash) {
        return hashOperations.hasKey(CACHE_NAME, hash);
    }
}

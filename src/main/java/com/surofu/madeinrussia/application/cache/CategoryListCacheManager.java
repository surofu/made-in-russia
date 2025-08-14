package com.surofu.madeinrussia.application.cache;

import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CategoryListCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "CATEGORY_LIST";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, List<CategoryDto>> listHashOperations;

    public CategoryListCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listHashOperations = redisTemplate.opsForHash();
    }

    public List<CategoryDto> getAllByLocale(String locale) {
        return listHashOperations.get(CACHE_NAME, locale);
    }

    public void setAllByLocale(String locale, List<CategoryDto> categoryDtos) {
        listHashOperations.put(CACHE_NAME, locale, categoryDtos);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void clearAll() {
        redisTemplate.delete(CACHE_NAME);
    }

    public boolean contains(String hash) {
        return listHashOperations.hasKey(CACHE_NAME, hash);
    }
}

package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.core.model.okved.OkvedCompany;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class CompanyCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "COMPANIES";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, List<OkvedCompany>> listHashOperations;

    public CompanyCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listHashOperations = redisTemplate.opsForHash();
    }

    public List<OkvedCompany> get(String categorySlug, Locale locale) {
        return listHashOperations.get(CACHE_NAME, createKey(categorySlug, locale));
    }

    public void set(String categorySlug, Locale locale, List<OkvedCompany> value) {
        listHashOperations.put(CACHE_NAME, createKey(categorySlug, locale), value);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public boolean contains(String categorySlug, Locale locale) {
        return listHashOperations.hasKey(CACHE_NAME, createKey(categorySlug, locale));
    }

    private String createKey(String categorySlug, Locale locale) {
        return categorySlug + "-" + locale.toLanguageTag();
    }
}

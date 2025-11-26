package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.advertisement.AdvertisementDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Component
public class AdvertisementCacheManager {
    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "ADVERTISEMENT";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, AdvertisementDto> hashOperations;

    public AdvertisementCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public List<AdvertisementDto> getAll() {
        return (List<AdvertisementDto>) hashOperations.entries(CACHE_NAME).values();
    }

    public AdvertisementDto getById(Long id, Locale locale) {
        return hashOperations.get(CACHE_NAME, getHash(id, locale));
    }

    public void set(Long id, AdvertisementDto advertisement, Locale locale) {
        hashOperations.put(CACHE_NAME, getHash(id, locale), advertisement);
        redisTemplate.expire(CACHE_NAME, ttl);
    }

    public void remove(Long id) {
        hashOperations.delete(CACHE_NAME, getHash(id, Locale.forLanguageTag("en")));
        hashOperations.delete(CACHE_NAME, getHash(id, Locale.forLanguageTag("ru")));
        hashOperations.delete(CACHE_NAME, getHash(id, Locale.forLanguageTag("zh")));
        hashOperations.delete(CACHE_NAME, getHash(id, Locale.forLanguageTag("hi")));
    }

    private String getHash(Long id, Locale locale) {
        return id.toString() + "_" + locale.getLanguage();
    }
}

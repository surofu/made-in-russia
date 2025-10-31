package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.WebLocalizationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
public class WebLocalizationCacheManager {

    @Value("${app.redis.ttl-duration}")
    private Duration ttl;

    private final String CACHE_NAME = "WEB_LOCALIZATION";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, WebLocalizationDto> hashOperations;

    public WebLocalizationCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public Map<String, WebLocalizationDto> getAll() {
        return hashOperations.entries(CACHE_NAME);
    }

    public WebLocalizationDto getWebLocalization(String languageCode) {
        return hashOperations.get(CACHE_NAME, languageCode);
    }

    public void setWebLocalization(String languageCode, WebLocalizationDto webLocalization) {
        hashOperations.put(CACHE_NAME, languageCode, webLocalization);
        redisTemplate.expire(CACHE_NAME, ttl);
    }

    public void removeWebLocalization(String languageCode) {
        hashOperations.delete(CACHE_NAME, languageCode);
    }

    public void clearAll() {
        redisTemplate.delete(CACHE_NAME);
    }
}

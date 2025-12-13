package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.category.CategoryDto;
import java.time.Duration;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CategoryCacheManager {
  private final String CACHE_NAME = "CATEGORY";
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, CategoryDto> hashOperations;
  @Value("${app.redis.ttl-duration}")
  private Duration ttl;

  public CategoryCacheManager(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  public CategoryDto getCategory(Long id, Locale locale) {
    String hash = createHash(id, locale);
    return hashOperations.get(CACHE_NAME, hash);
  }

  public CategoryDto getCategory(String slug, Locale locale) {
    String hash = createHash(slug, locale);
    return hashOperations.get(CACHE_NAME, hash);
  }

  public void setCategory(Long id, Locale locale, CategoryDto category) {
    String hash = createHash(id, locale);
    hashOperations.put(CACHE_NAME, hash, category);
    redisTemplate.expire(CACHE_NAME, ttl);
  }

  public void setCategory(String slug, Locale locale, CategoryDto category) {
    String hash = createHash(slug, locale);
    hashOperations.put(CACHE_NAME, hash, category);
    redisTemplate.expire(CACHE_NAME, ttl);
  }

  public void clear() {
    redisTemplate.delete(CACHE_NAME);
  }

  public boolean contains(Long id, Locale locale) {
    String hash = createHash(id, locale);
    return hashOperations.hasKey(CACHE_NAME, hash);
  }

  private String createHash(Long id, Locale locale) {
    return id.toString().concat("_").concat(locale.getLanguage());
  }

  private String createHash(String slug, Locale locale) {
    return slug.concat("_").concat(locale.getLanguage());
  }
}

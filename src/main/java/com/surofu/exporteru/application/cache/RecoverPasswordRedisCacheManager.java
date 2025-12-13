package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.dto.auth.RecoverPasswordDto;
import com.surofu.exporteru.core.model.user.UserEmail;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public final class RecoverPasswordRedisCacheManager {
  private final String CACHE_NAME = "RECOVER_PASSWORD";
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, RecoverPasswordDto> hashOperations;
  @Value("${app.redis.verification-ttl-duration}")
  private Duration ttl;

  public RecoverPasswordRedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
  }

  public RecoverPasswordDto get(UserEmail userEmail) {
    return hashOperations.get(CACHE_NAME, userEmail.toString());
  }

  public void set(UserEmail userEmail, RecoverPasswordDto dto) {
    hashOperations.put(CACHE_NAME, userEmail.toString(), dto);
    redisTemplate.expire(CACHE_NAME, ttl);
  }

  public void clear(UserEmail userEmail) {
    hashOperations.delete(CACHE_NAME, userEmail.toString());
  }
}

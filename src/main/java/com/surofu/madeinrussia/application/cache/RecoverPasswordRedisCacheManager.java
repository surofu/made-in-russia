package com.surofu.madeinrussia.application.cache;

import com.surofu.madeinrussia.application.dto.auth.RecoverPasswordDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public final class RecoverPasswordRedisCacheManager {
    @Value("${app.redis.verification-ttl-duration}")
    private Duration ttl;
    private final String CACHE_NAME = "RECOVER_PASSWORD";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, RecoverPasswordDto> hashOperations;

    public RecoverPasswordRedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Nullable
    public RecoverPasswordDto getPassword(UserEmail userEmail) {
        return hashOperations.get(CACHE_NAME, userEmail.toString());
    }

    public void setPasswordWithTtl(UserEmail userEmail, RecoverPasswordDto recoverPasswordDto) {
        hashOperations.put(CACHE_NAME, userEmail.toString(), recoverPasswordDto);
        redisTemplate.expire(CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void clear(UserEmail userEmail) {
       hashOperations.delete(CACHE_NAME, userEmail.toString());
    }
}

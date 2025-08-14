package com.surofu.madeinrussia.application.cache;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public final class UserVerificationRedisCacheManager {
    @Value("${app.redis.verification-ttl-duration}")
    private Duration ttl;

    private final String VERIFICATION_USER_CACHE_NAME = "VERIFICATION_USER";
    private final String VERIFICATION_PASSWORD_CACHE_NAME = "VERIFICATION_PASSWORD";
    private final String VERIFICATION_CODE_CACHE_NAME = "VERIFICATION_CODE";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, User> userHashOperations;
    private final HashOperations<String, String, UserPassword> passwordHashOperations;
    private final HashOperations<String, String, String> codeHashOperations;

    public UserVerificationRedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.userHashOperations = redisTemplate.opsForHash();
        this.passwordHashOperations = redisTemplate.opsForHash();
        this.codeHashOperations = redisTemplate.opsForHash();
    }

    @Nullable
    public User getUser(UserEmail userEmail) {
        return userHashOperations.get(VERIFICATION_USER_CACHE_NAME, userEmail.toString());
    }

    @Nullable
    public UserPassword getUserPassword(UserEmail userEmail) {
        return passwordHashOperations.get(VERIFICATION_PASSWORD_CACHE_NAME, userEmail.toString());
    }

    @Nullable
    public String getVerificationCode(UserEmail userEmail) {
        return codeHashOperations.get(VERIFICATION_CODE_CACHE_NAME, userEmail.toString());
    }

    public void setUser(UserEmail userEmail, User user) {
        userHashOperations.put(VERIFICATION_USER_CACHE_NAME, userEmail.toString(), user);
        redisTemplate.expire(VERIFICATION_USER_CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void setUserPassword(UserEmail userEmail, UserPassword userPassword) {
        passwordHashOperations.put(VERIFICATION_PASSWORD_CACHE_NAME, userEmail.toString(), userPassword);
        redisTemplate.expire(VERIFICATION_PASSWORD_CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void setVerificationCode(UserEmail userEmail, String verificationCode) {
        codeHashOperations.put(VERIFICATION_CODE_CACHE_NAME, userEmail.toString(), verificationCode);
        redisTemplate.expire(VERIFICATION_CODE_CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    public void clearCache(UserEmail userEmail) {
        userHashOperations.delete(VERIFICATION_USER_CACHE_NAME, userEmail.toString());
        passwordHashOperations.delete(VERIFICATION_PASSWORD_CACHE_NAME, userEmail.toString());
        codeHashOperations.delete(VERIFICATION_CODE_CACHE_NAME, userEmail.toString());
    }
}

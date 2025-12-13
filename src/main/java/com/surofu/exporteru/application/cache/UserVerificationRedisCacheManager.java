package com.surofu.exporteru.application.cache;

import com.surofu.exporteru.application.exception.CacheEntityNotFoundException;
import com.surofu.exporteru.application.exception.OutOfAttemptsException;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.password.UserPassword;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public final class UserVerificationRedisCacheManager {
  private final String VERIFICATION_USER_CACHE_NAME = "VERIFICATION_USER";
  private final String VERIFICATION_PASSWORD_CACHE_NAME = "VERIFICATION_PASSWORD";
  private final String VERIFICATION_CODE_CACHE_NAME = "VERIFICATION_CODE";
  private final RedisTemplate<String, Object> redisTemplate;
  private final HashOperations<String, String, UserWithAttempts> userHashOperations;
  private final HashOperations<String, String, UserPassword> passwordHashOperations;
  private final HashOperations<String, String, VerificationCodeWithAttempts> codeHashOperations;
  @Value("${app.redis.verification-ttl-duration}")
  private Duration ttl;

  public UserVerificationRedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.userHashOperations = redisTemplate.opsForHash();
    this.passwordHashOperations = redisTemplate.opsForHash();
    this.codeHashOperations = redisTemplate.opsForHash();
  }

  public User getUser(UserEmail userEmail)
      throws OutOfAttemptsException, CacheEntityNotFoundException {
    UserWithAttempts userWithAttempts =
        userHashOperations.get(VERIFICATION_USER_CACHE_NAME, userEmail.toString());

    if (userWithAttempts == null) {
      throw new CacheEntityNotFoundException();
    }

    try {
      User user = userWithAttempts.getUser();
      userHashOperations.put(VERIFICATION_USER_CACHE_NAME, userEmail.toString(), userWithAttempts);
      return user;
    } catch (OutOfAttemptsException e) {
      userHashOperations.delete(VERIFICATION_USER_CACHE_NAME, userEmail.toString());
      throw e;
    }
  }

  @Nullable
  public UserPassword getUserPassword(UserEmail userEmail) {
    return passwordHashOperations.get(VERIFICATION_PASSWORD_CACHE_NAME, userEmail.toString());
  }

  public String getVerificationCode(UserEmail userEmail)
      throws OutOfAttemptsException, CacheEntityNotFoundException {
    VerificationCodeWithAttempts verificationCodeWithAttempts =
        codeHashOperations.get(VERIFICATION_CODE_CACHE_NAME, userEmail.toString());

    if (verificationCodeWithAttempts == null) {
      throw new CacheEntityNotFoundException();
    }

    try {
      String verificationCode = verificationCodeWithAttempts.getVerificationCode();
      codeHashOperations.put(VERIFICATION_CODE_CACHE_NAME, userEmail.toString(),
          verificationCodeWithAttempts);
      return verificationCode;
    } catch (OutOfAttemptsException e) {
      codeHashOperations.delete(VERIFICATION_CODE_CACHE_NAME, userEmail.toString());
      throw e;
    }
  }

  public void setUser(UserEmail userEmail, User user) {
    UserWithAttempts userWithAttempts = new UserWithAttempts(user);
    userHashOperations.put(VERIFICATION_USER_CACHE_NAME, userEmail.toString(), userWithAttempts);
    redisTemplate.expire(VERIFICATION_USER_CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
  }

  public void setUserPassword(UserEmail userEmail, UserPassword userPassword) {
    passwordHashOperations.put(VERIFICATION_PASSWORD_CACHE_NAME, userEmail.toString(),
        userPassword);
    redisTemplate.expire(VERIFICATION_PASSWORD_CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
  }

  public void setVerificationCode(UserEmail userEmail, String verificationCode) {
    codeHashOperations.put(VERIFICATION_CODE_CACHE_NAME, userEmail.toString(),
        new VerificationCodeWithAttempts(verificationCode));
    redisTemplate.expire(VERIFICATION_CODE_CACHE_NAME, ttl.getSeconds(), TimeUnit.SECONDS);
  }

  public void clearCache(UserEmail userEmail) {
    userHashOperations.delete(VERIFICATION_USER_CACHE_NAME, userEmail.toString());
    passwordHashOperations.delete(VERIFICATION_PASSWORD_CACHE_NAME, userEmail.toString());
    codeHashOperations.delete(VERIFICATION_CODE_CACHE_NAME, userEmail.toString());
  }

  private static class UserWithAttempts implements Serializable {
    private final User user;
    private int attempts;

    public UserWithAttempts(User user) {
      this.user = user;
      this.attempts = 5;
    }

    public User getUser() throws OutOfAttemptsException {
      if (this.attempts > 0) {
        this.attempts--;
        return this.user;
      }

      throw new OutOfAttemptsException();
    }
  }

  private static class VerificationCodeWithAttempts implements Serializable {
    private final String verificationCode;
    private int attempts;

    public VerificationCodeWithAttempts(String verificationCode) {
      this.verificationCode = verificationCode;
      this.attempts = 5;
    }

    public String getVerificationCode() throws OutOfAttemptsException {
      if (this.attempts > 0) {
        this.attempts--;
        return this.verificationCode;
      }

      throw new OutOfAttemptsException();
    }
  }
}

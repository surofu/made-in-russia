package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import jakarta.annotation.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

@Component
public final class UserVerificationCaffeineCacheManager extends CaffeineCacheManager {
    private final String CACHE_NAME = "temp_user_email_verification_cache";
    private final String USER_PASSWORD_CACHE_NAME_TEMPLATE = "%s-password";
    private final String VERIFICATION_CODE_CACHE_NAME_TEMPLATE = "%s-verification-code";

    private final Cache CACHE;

    public UserVerificationCaffeineCacheManager() {
        super();
        this.CACHE = super.createCaffeineCache(CACHE_NAME);
    }

    public String getCacheName() {
        return CACHE_NAME;
    }

    @Nullable
    public Cache getCache() {
        return getCache(CACHE_NAME);
    }

    @Nullable
    public User getUser(UserEmail userEmail) {
        return CACHE.get(userEmail.toString(), User.class);
    }

    @Nullable
    public UserPassword getUserPassword(UserEmail userEmail) {
        return CACHE.get(String.format(USER_PASSWORD_CACHE_NAME_TEMPLATE, userEmail), UserPassword.class);
    }

    @Nullable
    public String getVerificationCode(UserEmail userEmail) {
        return CACHE.get(String.format(VERIFICATION_CODE_CACHE_NAME_TEMPLATE, userEmail), String.class);
    }

    public void setUser(UserEmail userEmail, User user) {
        CACHE.put(userEmail.toString(), user);
    }

    public void setUserPassword(UserEmail userEmail, UserPassword userPassword) {
        CACHE.put(String.format(USER_PASSWORD_CACHE_NAME_TEMPLATE, userEmail), userPassword);
    }

    public void setVerificationCode(UserEmail userEmail, String verificationCode) {
        CACHE.put(String.format(VERIFICATION_CODE_CACHE_NAME_TEMPLATE, userEmail), verificationCode);
    }

    public void clearCache(UserEmail userEmail) {
        CACHE.evict(userEmail);
        CACHE.evict(String.format(USER_PASSWORD_CACHE_NAME_TEMPLATE, userEmail));
        CACHE.evict(String.format(VERIFICATION_CODE_CACHE_NAME_TEMPLATE, userEmail));
    }
}

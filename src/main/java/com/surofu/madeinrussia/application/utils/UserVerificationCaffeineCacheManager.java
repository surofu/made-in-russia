package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
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

    public Cache getCache() {
        return getCache(CACHE_NAME);
    }

    public User getUser(UserEmail userEmail) {
        return CACHE.get(userEmail.toString(), User.class);
    }

    public UserPassword getUserPassword(UserEmail userEmail) {
        return CACHE.get(String.format(USER_PASSWORD_CACHE_NAME_TEMPLATE, userEmail.toString()), UserPassword.class);
    }

    public String getVerificationCode(UserEmail userEmail) {
        return CACHE.get(String.format(VERIFICATION_CODE_CACHE_NAME_TEMPLATE, userEmail.toString()), String.class);
    }

    public void setUser(UserEmail userEmail, User user) {
        CACHE.put(userEmail.toString(), user);
    }

    public void setUserPassword(UserEmail userEmail, UserPassword userPassword) {
        CACHE.put(String.format(USER_PASSWORD_CACHE_NAME_TEMPLATE, userEmail.toString()), userPassword);
    }

    public void setVerificationCode(UserEmail userEmail, String verificationCode) {
        CACHE.put(String.format(VERIFICATION_CODE_CACHE_NAME_TEMPLATE, userEmail.toString()), verificationCode);
    }

    public void clearCache(UserEmail userEmail) {
        CACHE.evict(userEmail);
        CACHE.evict(String.format(USER_PASSWORD_CACHE_NAME_TEMPLATE, userEmail));
        CACHE.evict(String.format(VERIFICATION_CODE_CACHE_NAME_TEMPLATE, userEmail));
    }
}

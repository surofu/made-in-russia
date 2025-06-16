package com.surofu.madeinrussia.application.utils;

import com.surofu.madeinrussia.application.dto.RecoverPasswordDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

@Component
public final class RecoverPasswordCaffeineCacheManager extends CaffeineCacheManager {
    private final String CACHE_NAME = "temp_recover_user_password";

    private final Cache CACHE;

    public RecoverPasswordCaffeineCacheManager() {
        super();
        this.CACHE = super.createCaffeineCache(CACHE_NAME);
    }

    public String getCacheName() {
        return CACHE_NAME;
    }

    public Cache getCache() {
        return getCache(CACHE_NAME);
    }

    public RecoverPasswordDto getRecoverPasswordDto(UserEmail userEmail) {
        return CACHE.get(userEmail.toString(), RecoverPasswordDto.class);
    }

    public void setRecoverPasswordDto(UserEmail userEmail, RecoverPasswordDto recoverPasswordDto) {
        CACHE.put(userEmail.toString(), recoverPasswordDto);
    }

    public void clearRecoverPasswordDto(UserEmail userEmail) {
        CACHE.evict(userEmail.toString());
    }
}

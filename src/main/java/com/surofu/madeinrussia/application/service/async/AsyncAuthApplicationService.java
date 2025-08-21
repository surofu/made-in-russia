package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.application.dto.auth.RecoverPasswordDto;
import com.surofu.madeinrussia.application.cache.RecoverPasswordRedisCacheManager;
import com.surofu.madeinrussia.application.cache.UserVerificationRedisCacheManager;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.service.auth.operation.RecoverPassword;
import com.surofu.madeinrussia.core.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAuthApplicationService {

    private final UserPasswordRepository passwordRepository;

    private final RecoverPasswordRedisCacheManager recoverPasswordRedisCacheManager;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveUserPasswordInDatabaseAndClearRecoverPasswordCacheByUserEmail(UserPassword userPassword, UserEmail userEmail) {
        try {
            passwordRepository.saveUserPassword(userPassword);
            recoverPasswordRedisCacheManager.clear(userEmail);
        } catch (Exception e) {
            log.error("Error saving user password: {}", e.getMessage(), e);
        }
    }
}

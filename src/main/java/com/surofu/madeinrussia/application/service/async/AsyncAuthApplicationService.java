package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.model.userPassword.UserPasswordPassword;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import com.surofu.madeinrussia.core.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAuthApplicationService {
    private final UserRepository userRepository;
    private final UserPasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final CacheManager verificationCacheManager;

    @Async
    @Transactional
    public CompletableFuture<Void> saveRegisterDataInCacheAndSendVerificationCodeToEmail(Register operation) throws CompletionException {
        String rawEmail = operation.getCommand().email();
        String rawLogin = operation.getCommand().login();
        String rawPhoneNumber = operation.getCommand().phoneNumber();
        String rawPassword = operation.getCommand().password();
        String rawHashedPassword = passwordEncoder.encode(rawPassword);
        String rawRegion = operation.getCommand().region();

        UserEmail userEmail = UserEmail.of(rawEmail);
        UserLogin userLogin = UserLogin.of(rawLogin);
        UserPhoneNumber userPhoneNumber = UserPhoneNumber.of(rawPhoneNumber);
        UserPasswordPassword userPasswordPassword = UserPasswordPassword.of(rawHashedPassword);
        UserRegion userRegion = UserRegion.of(rawRegion);

        User user = new User();
        user.setRole(UserRole.ROLE_USER);
        user.setEmail(userEmail);
        user.setLogin(userLogin);
        user.setPhoneNumber(userPhoneNumber);
        user.setRegion(userRegion);

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);
        userPassword.setPassword(userPasswordPassword);

        String unverifiedUsersCacheName = "unverifiedUsers";
        String unverifiedUserPasswordsCacheName = "unverifiedUserPasswords";
        String verificationCodesCacheName = "verificationCodes";

        Cache unverifiedUsersCache = getCacheSafe(unverifiedUsersCacheName);
        Cache unverifiedUserPasswordsCache = getCacheSafe(unverifiedUserPasswordsCacheName);
        Cache verificationCodesCache = getCacheSafe(verificationCodesCacheName);

        unverifiedUsersCache.put(rawEmail, user);
        unverifiedUserPasswordsCache.put(rawEmail, userPassword);

        String verificationCode = generateVerificationCode();
        verificationCodesCache.put(rawEmail, verificationCode);

        String expiration = "через 30 минут";

        String messageSubject = "Подтверждение вашей электронной почты для MadeInRussia";
        String messageText = String.format("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Подтверждение электронной почты</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        }
                        h1 {
                            font-size: 72px;
                            color: #4CAF50;
                            text-align: center;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                        }
                        img {
                            display: block;
                            margin: 0 auto 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>Здравствуйте!</h2>
                        <p>Спасибо за регистрацию в MadeInRussia! Чтобы завершить процесс регистрации, пожалуйста, подтвердите свою электронную почту, введя код ниже:</p>
                       \s
                        <strong>Ваш код подтверждения:</strong>
                        <h1>%s</h1>
                       \s
                        <p>Пожалуйста, введите этот код в соответствующее поле на нашем сайте. Если вы не регистрировались в MadeInRussia, просто проигнорируйте это сообщение.</p>
                       \s
                        <p>Код истечет %s</p>
                       \s
                        <p>Спасибо, что выбрали MadeInRussia!</p>
                       \s
                        <div class="footer">
                            <p>С уважением,<br>Команда MadeInRussia</p>
                        </div>
                    </div>
                </body>
                </html>
                """, verificationCode, expiration);

        try {
            mailService.sendEmail(rawEmail, messageSubject, messageText);
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> saveUserInDatabaseAndRemoveFromCache(User user, UserPassword userPassword) throws CompletionException {
        String unverifiedUsersCacheName = "unverifiedUsers";
        String unverifiedUserPasswordsCacheName = "unverifiedUserPasswords";
        String verificationCodesCacheName = "verificationCodes";

        Cache unverifiedUsersCache = getCacheSafe(unverifiedUsersCacheName);
        Cache unverifiedUserPasswordsCache = getCacheSafe(unverifiedUserPasswordsCacheName);
        Cache verificationCodesCache = getCacheSafe(verificationCodesCacheName);

        String email = user.getEmail().getValue();

        unverifiedUsersCache.evict(email);
        unverifiedUserPasswordsCache.evict(email);
        verificationCodesCache.evict(email);

        userRepository.saveUser(user);
        passwordRepository.saveUserPassword(userPassword);

        return CompletableFuture.completedFuture(null);
    }

    private Cache getCacheSafe(String cacheName) throws CompletionException {
        Cache cache = verificationCacheManager.getCache(cacheName);

        if (cache == null) {
            throw CacheNotFoundException.of(cacheName);
        }

        return cache;
    }

    private String generateVerificationCode() {
        StringBuilder verificationCode = new StringBuilder();
        Random random = new Random();

        int CODE_LENGTH = 4;
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomInt = random.nextInt(10);
            verificationCode.append(randomInt);
        }

        log.info("Generated verification code: {}", verificationCode);
        return verificationCode.toString();
    }

    private static final class CacheNotFoundException extends CompletionException {

        private CacheNotFoundException(String cacheName) {
            super(String.format("Error while sending email. Cache with name '%s' not found", cacheName));
        }

        public static CacheNotFoundException of(String cacheName) {
            return new CacheNotFoundException(cacheName);
        }
    }
}

package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.application.utils.EmailVerificationUtils;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import com.surofu.madeinrussia.core.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAuthApplicationService {
    private final UserRepository userRepository;
    private final UserPasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationUtils emailVerificationUtils;
    private final MailService mailService;

    @Qualifier("verificationCacheManager")
    private final CacheManager cacheManager;

    @Async
    public CompletableFuture<Void> saveRegisterDataInCacheAndSendVerificationCodeToEmail(Register operation) {
        String rawEmail = operation.getCommand().email();
        Optional<String> rawLogin = operation.getCommand().login();

        UserEmail userEmail = UserEmail.of(rawEmail);
        Optional<UserLogin> userLogin = rawLogin.map(UserLogin::of);

        User user = new User();
        user.setEmail(userEmail);
        user.setLogin(userLogin.orElse(null));

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);

        String rawHashedPassword = passwordEncoder.encode(operation.getCommand().password());
        userPassword.setPassword(rawHashedPassword);

        user.setRole(UserRole.ROLE_USER);

        Cache unverifiedUsersCache = cacheManager.getCache("unverifiedUsers");
        Cache unverifiedUserPasswordsCache = cacheManager.getCache("unverifiedUserPasswords");
        Cache verificationCodesCache = cacheManager.getCache("verificationCodes");

        unverifiedUsersCache.put(rawEmail, user);
        unverifiedUserPasswordsCache.put(rawEmail, userPassword);

        String verificationCode = emailVerificationUtils.generateVerificationCode();
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
            log.error("Error while sending email", ex);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> saveUserInDatabaseAndRemoveFromCache(User user, UserPassword userPassword) {
        Cache unverifiedUsersCache = cacheManager.getCache("unverifiedUsers");
        Cache unverifiedUserPasswordsCache = cacheManager.getCache("unverifiedUserPasswords");
        Cache verificationCodesCache = cacheManager.getCache("verificationCodes");

        String email = user.getEmail().getEmail();

        unverifiedUsersCache.evict(email);
        unverifiedUserPasswordsCache.evict(email);
        verificationCodesCache.evict(email);

        userRepository.saveUser(user);
        passwordRepository.saveUserPassword(userPassword);

        return CompletableFuture.completedFuture(null);
    }
}

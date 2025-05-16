package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.VerifyEmailSuccessDto;
import com.surofu.madeinrussia.application.model.SecurityUser;
import com.surofu.madeinrussia.application.model.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.model.userPassword.UserPasswordPassword;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.AuthService;
import com.surofu.madeinrussia.core.service.auth.operation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthApplicationService implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final AsyncAuthApplicationService asyncAuthApplicationService;
    private final AsyncSessionApplicationService asyncSessionApplicationService;

    private final CacheManager verificationCacheManager;

    @Override
    @Transactional
    public Register.Result register(Register operation) {
        String rawEmail = operation.getCommand().email();
        UserEmail userEmail = UserEmail.of(rawEmail);

        String rawLogin = operation.getCommand().login();
        UserLogin userLogin = UserLogin.of(rawLogin);

        if (userRepository.existsUserByEmail(userEmail)) {
            return Register.Result.userWithEmailAlreadyExists(userEmail);
        }

        if (userRepository.existsUserByLogin(userLogin)) {
            return Register.Result.userWithLoginAlreadyExists(userLogin);
        }

        String registerSuccessMessage = String.format("Код для подтверждения почты был отправлен на почту '%s'", rawEmail);

        SimpleResponseMessageDto registerSuccessMessageDto = SimpleResponseMessageDto.builder()
                .message(registerSuccessMessage)
                .build();

        asyncAuthApplicationService.saveRegisterDataInCacheAndSendVerificationCodeToEmail(operation);

        return Register.Result.success(registerSuccessMessageDto);
    }

    @Override
    @Transactional
    public LoginWithEmail.Result loginWithEmail(LoginWithEmail operation) {
        LoginSuccessDto loginSuccessDto;

        String rawEmail = operation.getCommand().email();
        String rawPassword = operation.getCommand().password();

        UserEmail userEmail = UserEmail.of(rawEmail);
        UserPasswordPassword userPasswordPassword = UserPasswordPassword.of(rawPassword);

        try {
            loginSuccessDto = login(rawEmail, rawPassword);
        } catch (AuthenticationException ex) {
            return LoginWithEmail.Result.invalidCredentials(userEmail, userPasswordPassword);
        }

        return LoginWithEmail.Result.success(loginSuccessDto);
    }

    @Override
    @Transactional
    public LoginWithLogin.Result loginWithLogin(LoginWithLogin operation) {
        String rawLogin = operation.getCommand().login();
        UserLogin userLogin = UserLogin.of(rawLogin);

        String rawPassword = operation.getCommand().password();
        UserPasswordPassword userPasswordPassword = UserPasswordPassword.of(rawPassword);

        Optional<UserEmail> userEmail = userRepository.getUserEmailByLogin(userLogin);

        if (userEmail.isEmpty()) {
            return LoginWithLogin.Result.invalidCredentials(userLogin, userPasswordPassword);
        }

        String rawEmail = userEmail.get().getEmail();

        LoginSuccessDto loginSuccessDto;

        try {
            loginSuccessDto = login(rawEmail, rawPassword);
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed", ex);
            return LoginWithLogin.Result.invalidCredentials(userLogin, userPasswordPassword);
        }

        return LoginWithLogin.Result.success(loginSuccessDto);
    }

    @Override
    @Transactional
    public VerifyEmail.Result verifyEmail(VerifyEmail operation) {
        String rawEmail = operation.getVerifyEmailCommand().email();
        UserEmail userEmail = UserEmail.of(rawEmail);

        String verificationCode = operation.getVerifyEmailCommand().code();
        SessionInfo sessionInfo = operation.getSessionInfo();

        String unverifiedCacheName = "unverifiedUsers";
        Cache unverifiedUsersCache = verificationCacheManager.getCache(unverifiedCacheName);

        if (unverifiedUsersCache == null) {
            return VerifyEmail.Result.cacheNotFound(unverifiedCacheName);
        }

        User user = unverifiedUsersCache.get(rawEmail, User.class);

        if (user == null) {
            return VerifyEmail.Result.accountNotFound(userEmail);
        }

        String unverifiedUserPasswordsCacheName = "unverifiedUserPasswords";
        Cache unverifiedUserPasswordsCache = verificationCacheManager.getCache(unverifiedUserPasswordsCacheName);

        if (unverifiedUserPasswordsCache == null) {
            return VerifyEmail.Result.cacheNotFound(unverifiedUserPasswordsCacheName);
        }

        UserPassword userPassword = unverifiedUserPasswordsCache.get(rawEmail, UserPassword.class);

        if (userPassword == null) {
            return VerifyEmail.Result.accountNotFound(userEmail);
        }

        String verificationCodesCacheName = "verificationCodes";
        Cache verificationCodesCache = verificationCacheManager.getCache(verificationCodesCacheName);

        if (verificationCodesCache == null) {
            return VerifyEmail.Result.cacheNotFound(verificationCodesCacheName);
        }

        String verificationCodeFromCache = verificationCodesCache.get(rawEmail, String.class);

        if (verificationCodeFromCache == null) {
            return VerifyEmail.Result.accountNotFound(userEmail);
        }

        if (!verificationCode.equals(verificationCodeFromCache)) {
            return VerifyEmail.Result.invalidVerificationCode(verificationCode);
        }

        SecurityUser securityUser = new SecurityUser(user, userPassword, sessionInfo);

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        VerifyEmailSuccessDto verifyEmailSuccessDto = VerifyEmailSuccessDto.of(accessToken, refreshToken);

        asyncAuthApplicationService.saveUserInDatabaseAndRemoveFromCache(user, userPassword)
                .thenCompose(unused -> asyncSessionApplicationService
                        .saveOrUpdateSessionFromHttpRequest(securityUser)
                        .exceptionally(ex -> {
                            log.error("Error while saving session", ex);
                            return null;
                        })
                ).exceptionally(ex -> {
                    log.error("Error while saving user", ex);
                    return null;
                });

        return VerifyEmail.Result.success(verifyEmailSuccessDto);
    }

    @Override
    @Transactional
    public Logout.Result logout(Logout operation) {
        SessionInfo sessionInfo = operation.getSecurityUser().getSessionInfo();

        Long userId = operation.getSecurityUser().getUser().getId();

        String rawDeviceId = sessionInfo.getDeviceId();
        SessionDeviceId sessionDeviceId = SessionDeviceId.of(rawDeviceId);

        String message = "Успешный выход из аккаунта";
        SimpleResponseMessageDto responseMessage = SimpleResponseMessageDto.of(message);

        asyncSessionApplicationService.removeSessionByUserIdAndDeviceId(userId, sessionDeviceId)
                .exceptionally(ex -> {
                    log.error("Error while removing session", ex);
                    return null;
                });

        return Logout.Result.success(responseMessage);
    }

    private LoginSuccessDto login(String email, String password) throws AuthenticationException {
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        SecurityUser securityUser = (SecurityUser) authenticationResponse.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser);

        return LoginSuccessDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

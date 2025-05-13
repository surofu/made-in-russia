package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.VerifyEmailSuccessDto;
import com.surofu.madeinrussia.application.security.SecurityUser;
import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.model.userPassword.UserPasswordPassword;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.AuthService;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithEmail;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithLogin;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthApplicationService implements AuthService {
    private final UserRepository userRepository;
    private final UserPasswordRepository passwordRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final AsyncAuthApplicationService asyncAuthApplicationService;
    private final AsyncSessionApplicationService asyncSessionApplicationService;

    private final CacheManager verificationCacheManager;

    @Override
    public Register.Result register(Register operation) {
        String rawEmail = operation.getCommand().email();
        UserEmail userEmail = UserEmail.of(rawEmail);

        if (userRepository.existsUserByEmail(userEmail)) {
            return Register.Result.userWithEmailAlreadyExists(userEmail);
        }

        asyncAuthApplicationService.saveRegisterDataInCacheAndSendVerificationCodeToEmail(operation);

        String registerSuccessMessage = String.format("Код для подтверждения почты был отправлен на почту '%s'", rawEmail);

        SimpleResponseMessageDto registerSuccessMessageDto = SimpleResponseMessageDto.builder()
                .message(registerSuccessMessage)
                .build();

        return Register.Result.success(registerSuccessMessageDto);
    }

    @Override
    public LoginWithEmail.Result loginWithEmail(LoginWithEmail operation) {
        String email = operation.getCommand().email();
        String password = operation.getCommand().password();

        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(email, password);
        Authentication authenticationResponse;

        try {
            authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        } catch (AuthenticationException ex) {
            return LoginWithEmail.Result.invalidCredentials(email, password);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        UserDetails userDetails = (UserDetails) authenticationResponse.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        LoginSuccessDto loginSuccessDto = LoginSuccessDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return LoginWithEmail.Result.success(loginSuccessDto);
    }

    @Override
    public LoginWithLogin.Result loginWithLogin(LoginWithLogin operation) {
        String rawLogin = operation.getCommand().login();
        String rawPassword = operation.getCommand().password();

        UserLogin userLogin = UserLogin.of(rawLogin);
        UserPasswordPassword userPasswordPassword = UserPasswordPassword.of(rawPassword);

        Optional<UserEmail> userEmail = userRepository.getUserEmailByLogin(userLogin);

        if (userEmail.isEmpty()) {
            return LoginWithLogin.Result.invalidCredentials(userLogin, userPasswordPassword);
        }

        String rawEmail = userEmail.get().getEmail();

        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(rawEmail, rawPassword);
        Authentication authenticationResponse;

        try {
            authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed", ex);
            return LoginWithLogin.Result.invalidCredentials(userLogin, userPasswordPassword);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        UserDetails userDetails = (UserDetails) authenticationResponse.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        LoginSuccessDto loginSuccessDto = LoginSuccessDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return LoginWithLogin.Result.success(loginSuccessDto);
    }

    @Override
    public VerifyEmail.Result verifyEmail(VerifyEmail operation) {
        String email = operation.getVerifyEmailCommand().email();
        String verificationCode = operation.getVerifyEmailCommand().code();

        String unverifiedCacheName = "unverifiedUsers";
        Cache unverifiedUsersCache = verificationCacheManager.getCache(unverifiedCacheName);

        if (unverifiedUsersCache == null) {
            return VerifyEmail.Result.cacheNotFound(unverifiedCacheName);
        }

        User user = unverifiedUsersCache.get(email, User.class);

        if (user == null) {
            return VerifyEmail.Result.accountNotFound(email);
        }

        String unverifiedUserPasswordsCacheName = "unverifiedUserPasswords";
        Cache unverifiedUserPasswordsCache = verificationCacheManager.getCache(unverifiedUserPasswordsCacheName);

        if (unverifiedUserPasswordsCache == null) {
            return VerifyEmail.Result.cacheNotFound(unverifiedUserPasswordsCacheName);
        }

        UserPassword userPassword = unverifiedUserPasswordsCache.get(email, UserPassword.class);

        if (userPassword == null) {
            return VerifyEmail.Result.accountNotFound(email);
        }

        String verificationCodesCacheName = "verificationCodes";
        Cache verificationCodesCache = verificationCacheManager.getCache(verificationCodesCacheName);

        if (verificationCodesCache == null) {
            return VerifyEmail.Result.cacheNotFound(verificationCodesCacheName);
        }

        String verificationCodeFromCache = verificationCodesCache.get(email, String.class);

        if (verificationCodeFromCache == null) {
            return VerifyEmail.Result.accountNotFound(email);
        }

        if (!verificationCode.equals(verificationCodeFromCache)) {
            return VerifyEmail.Result.invalidVerificationCode(verificationCode);
        }

        asyncAuthApplicationService.saveUserInDatabaseAndRemoveFromCache(user, userPassword)
                .thenCompose(unused -> asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(
                                operation.getSaveOrUpdateSessionCommand().userAgent(),
                                operation.getSaveOrUpdateSessionCommand().ipAddress(),
                                user
                        ).exceptionally(ex -> {
                            log.error("Error while saving session", ex);
                            return null;
                        })
                ).exceptionally(ex -> {
                    log.error("Error while saving user", ex);
                    return null;
                });

        SecurityUser securityUser = new SecurityUser(user, userPassword);

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        VerifyEmailSuccessDto verifyEmailSuccessDto = VerifyEmailSuccessDto.of(accessToken, refreshToken);
        return VerifyEmail.Result.success(verifyEmailSuccessDto);
    }
}

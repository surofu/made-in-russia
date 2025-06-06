package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.VerifyEmailSuccessDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.application.utils.UserVerificationCaffeineCacheManager;
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
@RequiredArgsConstructor
public class AuthApplicationService implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final AsyncAuthApplicationService asyncAuthApplicationService;
    private final AsyncSessionApplicationService asyncSessionApplicationService;

    private final UserVerificationCaffeineCacheManager userVerificationCaffeineCacheManager;

    @Override
    @Transactional
    public Register.Result register(Register operation) {

        if (userRepository.existsUserByEmail(operation.getUserEmail())) {
            return Register.Result.userWithEmailAlreadyExists(operation.getUserEmail());
        }

        if (userRepository.existsUserByLogin(operation.getUserLogin())) {
            return Register.Result.userWithLoginAlreadyExists(operation.getUserLogin());
        }

        if (userRepository.existsUserByPhoneNumber(operation.getUserPhoneNumber())) {
            return Register.Result.userWithPhoneNumberAlreadyExists(operation.getUserPhoneNumber());
        }

        String registerSuccessMessage = String.format("Код для подтверждения почты был отправлен на почту '%s'", operation.getUserEmail().toString());

        SimpleResponseMessageDto registerSuccessMessageDto = SimpleResponseMessageDto.builder()
                .message(registerSuccessMessage)
                .build();

        asyncAuthApplicationService.saveRegisterDataInCacheAndSendVerificationCodeToEmail(operation)
                .exceptionally(ex -> {
                    log.error("Error while saving register code", ex);
                    return null;
                });

        return Register.Result.success(registerSuccessMessageDto);
    }

    @Override
    @Transactional
    public RegisterVendor.Result registerVendor(RegisterVendor operation) {
        if (userRepository.existsUserByEmail(operation.getUserEmail())) {
            return RegisterVendor.Result.userWithEmailAlreadyExists(operation.getUserEmail());
        }

        if (userRepository.existsUserByLogin(operation.getUserLogin())) {
            return RegisterVendor.Result.userWithLoginAlreadyExists(operation.getUserLogin());
        }

        if (userRepository.existsUserByPhoneNumber(operation.getUserPhoneNumber())) {
            return RegisterVendor.Result.userWithPhoneNumberAlreadyExists(operation.getUserPhoneNumber());
        }

        String registerSuccessMessage = String.format("Код для подтверждения почты был отправлен на почту '%s'", operation.getUserEmail().toString());

        SimpleResponseMessageDto registerSuccessMessageDto = SimpleResponseMessageDto.builder()
                .message(registerSuccessMessage)
                .build();

        asyncAuthApplicationService.saveRegisterVendorDataInCacheAndSendVerificationCodeToEmail(operation)
                .exceptionally(ex -> {
                    log.error("Error while saving register code", ex);
                    return null;
                });

        return RegisterVendor.Result.success(registerSuccessMessageDto);
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
            loginSuccessDto = login(userEmail, userPasswordPassword);
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

        LoginSuccessDto loginSuccessDto;

        try {
            loginSuccessDto = login(userEmail.get(), userPasswordPassword);
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed", ex);
            return LoginWithLogin.Result.invalidCredentials(userLogin, userPasswordPassword);
        }

        return LoginWithLogin.Result.success(loginSuccessDto);
    }

    @Override
    @Transactional
    public VerifyEmail.Result verifyEmail(VerifyEmail operation) {
        String verificationCodeFromCache = userVerificationCaffeineCacheManager.getVerificationCode(operation.getUserEmail());

        if (verificationCodeFromCache == null) {
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        if (!verificationCodeFromCache.equals(operation.getVerificationCode())) {
            return VerifyEmail.Result.invalidVerificationCode(operation.getVerificationCode());
        }

        User user = userVerificationCaffeineCacheManager.getUser(operation.getUserEmail());

        if (user == null) {
            log.error("User with email {} not found in cache", operation.getUserEmail());
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        UserPassword userPassword = userVerificationCaffeineCacheManager.getUserPassword(operation.getUserEmail());

        if (userPassword == null) {
            log.error("User password with email {} not found in cache", operation.getUserEmail());
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        SecurityUser securityUser = new SecurityUser(user, userPassword, operation.getSessionInfo());

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
        SessionDeviceId sessionDeviceId = sessionInfo.getDeviceId();

        String message = "Успешный выход из аккаунта";
        SimpleResponseMessageDto responseMessage = SimpleResponseMessageDto.of(message);

        asyncSessionApplicationService.removeSessionByUserIdAndDeviceId(userId, sessionDeviceId)
                .exceptionally(ex -> {
                    log.error("Error while removing session", ex);
                    return null;
                });

        return Logout.Result.success(responseMessage);
    }

    private LoginSuccessDto login(UserEmail userEmail, UserPasswordPassword userPasswordPassword) throws AuthenticationException {
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(userEmail.toString(), userPasswordPassword.toString());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        SecurityUser securityUser = (SecurityUser) authenticationResponse.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser)
                .exceptionally(ex -> {
                    log.error("Error saving session", ex);
                    return null;
                });

        return LoginSuccessDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

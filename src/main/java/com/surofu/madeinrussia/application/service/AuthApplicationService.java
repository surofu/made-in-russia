package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.AuthService;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithEmail;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithLogin;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import com.surofu.madeinrussia.core.service.auth.operation.VerifyEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Service
@Transactional
@RequiredArgsConstructor
public class AuthApplicationService implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AsyncAuthApplicationService asyncAuthApplicationService;

    @Qualifier("verificationCacheManager")
    private final CacheManager cacheManager;

    @Override
    public Register.Result register(Register operation) {
        String rawEmail = operation.getCommand().email();
        Optional<String> rawLogin = operation.getCommand().login();

        UserEmail userEmail = UserEmail.of(rawEmail);
        Optional<UserLogin> userLogin = rawLogin.map(UserLogin::of);

        if (userRepository.existsUserByEmail(userEmail)) {
            return Register.Result.userWithEmailAlreadyExists(userEmail);
        }

        if (userLogin.isPresent() && userRepository.existsUserByLogin(userLogin.get())) {
            return Register.Result.userWithLoginAlreadyExists(userLogin.get());
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
            return LoginWithEmail.Result.invalidCredentials();
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
        String login = operation.getCommand().login();
        String password = operation.getCommand().password();

        Optional<String> email = userRepository.getUserEmailByLogin(UserLogin.of(login));

        if (email.isEmpty()) {
            return LoginWithLogin.Result.invalidCredentials();
        }

        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(email, password);
        Authentication authenticationResponse;

        try {
            authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        } catch (AuthenticationException ex) {
            return LoginWithLogin.Result.invalidCredentials();
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
        String email = operation.getCommand().email();
        String verificationCode = operation.getCommand().code();

        Cache unverifiedUsersCache = cacheManager.getCache("unverifiedUsers");

        User user = unverifiedUsersCache.get(email, User.class);

        if (user == null) {
            return VerifyEmail.Result.accountNotFound(email);
        }

        Cache unverifiedUserPasswordsCache = cacheManager.getCache("unverifiedUserPasswords");

        UserPassword userPassword = unverifiedUserPasswordsCache.get(email, UserPassword.class);

        if (userPassword == null) {
            return VerifyEmail.Result.accountNotFound(email);
        }

        Cache verificationCodesCache = cacheManager.getCache("verificationCodes");

        String verificationCodeFromCache = verificationCodesCache.get(email, String.class);

        if (verificationCodeFromCache == null) {
            return VerifyEmail.Result.accountNotFound(email);
        }

        System.out.println("code from cache: " + verificationCodeFromCache);

        if (verificationCode.equals(verificationCodeFromCache)) {
            asyncAuthApplicationService.saveUserInDatabaseAndRemoveFromCache(user, userPassword);

            String message = "Почта успешно подтверждена";
            SimpleResponseMessageDto responseMessageDto = new SimpleResponseMessageDto(message);
            return VerifyEmail.Result.success(responseMessageDto);
        }

        return VerifyEmail.Result.invalidVerificationCode(verificationCode);
    }
}

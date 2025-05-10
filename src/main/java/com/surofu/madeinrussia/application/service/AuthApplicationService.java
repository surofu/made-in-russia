package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.security.JwtUtil;
import com.surofu.madeinrussia.application.security.SecurityUser;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.model.userRole.UserRole;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.AuthService;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithEmail;
import com.surofu.madeinrussia.core.service.auth.operation.LoginWithLogin;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthApplicationService implements AuthService {
    private final UserRepository userRepository;
    private final UserPasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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

        User user = new User();
        user.setEmail(userEmail);
        user.setLogin(userLogin.orElse(null));

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);

        String rawHashedPassword = passwordEncoder.encode(operation.getCommand().password());
        userPassword.setPassword(rawHashedPassword);

        user.setRole(UserRole.ROLE_NOT_VERIFIED);

        userRepository.saveUser(user);
        passwordRepository.saveUserPassword(userPassword);

        String registerSuccessMessage = "Аккаунт успешно создан. Для активации аккаунта подтвердите почту";

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

        if (userDetails instanceof SecurityUser securityUser) {
            if (securityUser.getUser().getRole().equals(UserRole.ROLE_NOT_VERIFIED)) {
                return LoginWithEmail.Result.notVerified();
            }
        }

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

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

        if (userDetails instanceof SecurityUser securityUser) {
            if (securityUser.getUser().getRole().equals(UserRole.ROLE_NOT_VERIFIED)) {
                return LoginWithLogin.Result.notVerified();
            }
        }

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        LoginSuccessDto loginSuccessDto = LoginSuccessDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return LoginWithLogin.Result.success(loginSuccessDto);
    }
}

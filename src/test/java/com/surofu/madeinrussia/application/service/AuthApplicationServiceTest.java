package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.command.auth.RegisterCommand;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import org.instancio.Instancio;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.concurrent.CompletableFuture;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    AsyncAuthApplicationService asyncAuthApplicationService;

    @Mock
    AsyncSessionApplicationService asyncSessionApplicationService;

    @Mock
    CacheManager verificationCacheManager;

    @InjectMocks
    AuthApplicationService authApplicationService;

    @RepeatedTest(10)
    void register_WhenCommandValid_ReturnsSuccessResult() {
        // given
        ArgumentCaptor<UserEmail> userEmailArgumentCaptor = ArgumentCaptor.forClass(UserEmail.class);
        ArgumentCaptor<UserLogin> userLoginArgumentCaptor = ArgumentCaptor.forClass(UserLogin.class);
        ArgumentCaptor<Register> registerOperationArgumentCaptor = ArgumentCaptor.forClass(Register.class);

        doReturn(false).when(userRepository).existsUserByEmail(userEmailArgumentCaptor.capture());
        doReturn(false).when(userRepository).existsUserByLogin(userLoginArgumentCaptor.capture());
        doReturn(CompletableFuture.completedFuture(null))
                .when(asyncAuthApplicationService)
                .saveRegisterDataInCacheAndSendVerificationCodeToEmail(registerOperationArgumentCaptor.capture());

        // when
        RegisterCommand registerCommand = Instancio.of(RegisterCommand.class)
                .generate(field(RegisterCommand::email), generators -> generators.net().email())
                .generate(field(RegisterCommand::phoneNumber), generators -> generators.text().pattern("+#d#d#d#d#d#d#d#d#d#d#d#d"))
                .generate(field(RegisterCommand::password), generators -> generators.text().pattern("#a#a#a#a#a#a"))
                .create();

        System.out.println(registerCommand);

        Register registerOperation = Register.of(registerCommand);
        Register.Result registerResult = authApplicationService.register(registerOperation);

        // then
        assertNotNull(registerResult);
        assertInstanceOf(Register.Result.class, registerResult);
        assertInstanceOf(Register.Result.Success.class, registerResult);

        Register.Result.Success registerSuccessResult = (Register.Result.Success) registerResult;
        SimpleResponseMessageDto simpleResponseMessageDto = registerSuccessResult.getResponseMessageDto();

        assertNotNull(simpleResponseMessageDto);

        String registerSuccessMessage = String.format("Код для подтверждения почты был отправлен на почту '%s'", registerCommand.email());
        assertEquals(registerSuccessMessage, simpleResponseMessageDto.getMessage());

        verify(userRepository, times(1)).existsUserByEmail(userEmailArgumentCaptor.capture());
        verify(userRepository, times(1)).existsUserByLogin(userLoginArgumentCaptor.capture());
        verify(asyncAuthApplicationService, times(1)).saveRegisterDataInCacheAndSendVerificationCodeToEmail(registerOperation);
    }

    @Test
    void loginWithEmail() {
    }

    @Test
    void loginWithLogin() {
    }

    @Test
    void verifyEmail() {
    }

    @Test
    void logout() {
    }
}
package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
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

    @BeforeEach
    public void setup() {
        lenient().doNothing()
                .when(asyncAuthApplicationService).saveRecoverPasswordDataInCacheAndSendRecoverCodeToEmail(any());

        lenient().doReturn(CompletableFuture.completedFuture(null))
                .when(asyncAuthApplicationService).saveRegisterVendorDataInCacheAndSendVerificationCodeToEmail(any());

        lenient().doNothing()
                .when(asyncAuthApplicationService).saveRecoverPasswordDataInCacheAndSendRecoverCodeToEmail(any());

        lenient().doNothing()
                .when(asyncAuthApplicationService).saveUserPasswordInDatabaseAndClearRecoverPasswordCacheByUserEmail(any(), any());

        lenient().doNothing()
                .when(asyncSessionApplicationService).removeSessionById(anyLong());

        lenient().doReturn(CompletableFuture.completedFuture(null))
                .when(asyncSessionApplicationService).removeSessionByUserIdAndDeviceId(anyLong(), any());

        lenient().doReturn(CompletableFuture.completedFuture(null))
                .when(asyncSessionApplicationService).saveOrUpdateSessionFromHttpRequest(any());

        lenient().doReturn(CompletableFuture.completedFuture(null))
                .when(asyncAuthApplicationService).saveRegisterDataInCacheAndSendVerificationCodeToEmail(any());
    }

    @RepeatedTest(10)
    void register_WhenCommandValid_ReturnsSuccessResult() {
        // given
        ArgumentCaptor<UserEmail> userEmailArgumentCaptor = ArgumentCaptor.forClass(UserEmail.class);
        ArgumentCaptor<UserLogin> userLoginArgumentCaptor = ArgumentCaptor.forClass(UserLogin.class);
        ArgumentCaptor<UserPhoneNumber> userPhoneNumberArgumentCaptor = ArgumentCaptor.forClass(UserPhoneNumber.class);

        doReturn(false).when(userRepository).existsUserByEmail(userEmailArgumentCaptor.capture());
        doReturn(false).when(userRepository).existsUserByLogin(userLoginArgumentCaptor.capture());
        doReturn(false).when(userRepository).existsUserByPhoneNumber(userPhoneNumberArgumentCaptor.capture());

        // when
        UserEmail userEmail = Instancio.of(UserEmail.class)
                .generate(field(UserEmail::getValue), generators -> generators.net().email())
                .create();

        UserLogin userLogin = Instancio.of(UserLogin.class)
                .generate(field(UserLogin::getValue), generators -> generators.text().pattern("^[a-zA-Z0-9_-]+$"))
                .create();

        UserPhoneNumber userPhoneNumber = Instancio.of(UserPhoneNumber.class)
                .generate(field(UserPhoneNumber::getValue), generators -> generators.text().pattern("+#d#d#d#d#d#d#d#d#d#d#d#d"))
                .create();

        UserPasswordPassword userPassword = Instancio.of(UserPasswordPassword.class)
                .generate(field(UserPasswordPassword::getValue), generators -> generators.text().pattern("#a#a#a#a#a#a"))
                .create();

        UserRegion userRegion = Instancio.of(UserRegion.class)
                .generate(field(UserRegion::getValue), generators -> generators.text().pattern("#a#a#a#a#a#a"))
                .create();

        Register registerOperation = Register.of(userEmail, userLogin, userPassword, userRegion, userPhoneNumber);

        Register.Result registerResult = authApplicationService.register(registerOperation);

        // then
        assertNotNull(registerResult);
        assertInstanceOf(Register.Result.class, registerResult);
        assertInstanceOf(Register.Result.Success.class, registerResult);

        verify(userRepository, times(1)).existsUserByEmail(userEmailArgumentCaptor.capture());
        verify(userRepository, times(1)).existsUserByLogin(userLoginArgumentCaptor.capture());
        verify(asyncAuthApplicationService, times(1)).saveRegisterDataInCacheAndSendVerificationCodeToEmail(registerOperation);
    }
}
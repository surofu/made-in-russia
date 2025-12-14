package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.auth.LoginWithEmailCommand;
import com.surofu.exporteru.application.command.auth.RecoverPasswordCommand;
import com.surofu.exporteru.application.command.auth.RegisterCommand;
import com.surofu.exporteru.application.command.auth.RegisterVendorCommand;
import com.surofu.exporteru.application.command.auth.VerifyEmailCommand;
import com.surofu.exporteru.application.command.auth.VerifyRecoverPasswordCommand;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.auth.VerificationCode;
import com.surofu.exporteru.core.model.telegram.TelegramUser;
import com.surofu.exporteru.core.model.user.UserAvatar;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.model.user.UserRegion;
import com.surofu.exporteru.core.model.user.password.UserPasswordPassword;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsAddress;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.exporteru.core.service.auth.AuthService;
import com.surofu.exporteru.core.service.auth.operation.ForceRegister;
import com.surofu.exporteru.core.service.auth.operation.ForceRegisterVendor;
import com.surofu.exporteru.core.service.auth.operation.LoginWithEmail;
import com.surofu.exporteru.core.service.auth.operation.LoginWithTelegram;
import com.surofu.exporteru.core.service.auth.operation.Logout;
import com.surofu.exporteru.core.service.auth.operation.RecoverPassword;
import com.surofu.exporteru.core.service.auth.operation.Register;
import com.surofu.exporteru.core.service.auth.operation.RegisterVendor;
import com.surofu.exporteru.core.service.auth.operation.VerifyEmail;
import com.surofu.exporteru.core.service.auth.operation.VerifyRecoverPassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication")
public class AuthRestController {
  private final AuthService authService;
  private final Register.Result.Processor<ResponseEntity<?>> registerProcessor;
  private final RegisterVendor.Result.Processor<ResponseEntity<?>> registerVendorProcessor;
  private final LoginWithEmail.Result.Processor<ResponseEntity<?>> loginWithEmailProcessor;
  private final LoginWithTelegram.Result.Processor<ResponseEntity<?>> loginWithTelegramProcessor;
  private final VerifyEmail.Result.Processor<ResponseEntity<?>> verifyEmailProcessor;
  private final Logout.Result.Processor<ResponseEntity<?>> logoutProcessor;
  private final RecoverPassword.Result.Processor<ResponseEntity<?>> recoverPasswordProcessor;
  private final VerifyRecoverPassword.Result.Processor<ResponseEntity<?>>
      verifyRecoverPasswordProcessor;
  private final ForceRegister.Result.Processor<ResponseEntity<?>> forceRegisterProcessor;
  private final ForceRegisterVendor.Result.Processor<ResponseEntity<?>>
      forceRegisterVendorProcessor;

  @PostMapping("register")
  @Operation(summary = "Register new user")
  public ResponseEntity<?> register(@RequestBody RegisterCommand command) {
    Locale locale = LocaleContextHolder.getLocale();
    Register operation = Register.of(
        new UserEmail(command.email()),
        new UserLogin(command.login(), new HashMap<>()),
        new UserPasswordPassword(command.password()),
        new UserRegion(command.region()),
        new UserPhoneNumber(command.phoneNumber()),
        new UserAvatar(command.avatarUrl()),
        locale
    );
    return authService.register(operation).process(registerProcessor);
  }

  @PostMapping("register-vendor")
  @Operation(summary = "Register new vendor")
  public ResponseEntity<?> registerVendor(@RequestBody RegisterVendorCommand command) {
    Locale locale = LocaleContextHolder.getLocale();
    List<String> vendorCountryList =
        Objects.requireNonNullElse(command.countries(), new ArrayList<>());
    String region = vendorCountryList.isEmpty() ? "" : vendorCountryList.get(0);
    List<String> vendorProductCategoryList =
        Objects.requireNonNullElse(command.productCategories(), new ArrayList<>());
    RegisterVendor operation = RegisterVendor.of(
        new UserEmail(command.email()),
        new UserLogin(command.login(), new HashMap<>()),
        new UserPasswordPassword(command.password()),
        new UserRegion(region),
        new UserPhoneNumber(command.phoneNumber()),
        new UserAvatar(command.avatarUrl()),
        new VendorDetailsInn(command.inn()),
        new VendorDetailsAddress(command.address(), new HashMap<>()),
        vendorCountryList.stream().map(c -> new VendorCountryName(c, new HashMap<>())).toList(),
        vendorProductCategoryList.stream()
            .map(c -> new VendorProductCategoryName(c, new HashMap<>())).toList(),
        locale
    );
    return authService.registerVendor(operation).process(registerVendorProcessor);
  }

  @PostMapping("login-with-email")
  @Operation(summary = "Login with email")
  public ResponseEntity<?> loginWithEmail(@RequestBody @Valid LoginWithEmailCommand command) {
    LoginWithEmail operation = LoginWithEmail.of(
        new UserEmail(command.email()),
        new UserPasswordPassword(command.password())
    );
    return authService.loginWithEmail(operation).process(loginWithEmailProcessor);
  }

  @PostMapping("login-with-telegram")
  public ResponseEntity<?> loginWithTelegram(
      @RequestBody TelegramUser telegramUser,
      HttpServletRequest request
  ) {
    SessionInfo sessionInfo = SessionInfo.of(request);
    LoginWithTelegram operation = LoginWithTelegram.of(telegramUser, sessionInfo);
    return authService.loginWithTelegram(operation).process(loginWithTelegramProcessor);
  }

  @PostMapping("verify-email")
  @Operation(summary = "Verify email address")
  public ResponseEntity<?> verifyEmail(
      @RequestBody @Valid VerifyEmailCommand verifyEmailCommand,
      HttpServletRequest request
  ) {
    SessionInfo sessionInfo = SessionInfo.of(request);
    VerifyEmail operation = VerifyEmail.of(
        new UserEmail(verifyEmailCommand.email()),
        new VerificationCode(verifyEmailCommand.code()),
        sessionInfo
    );
    return authService.verifyEmail(operation).process(verifyEmailProcessor);
  }

  @PostMapping("logout")
  @PreAuthorize("isAuthenticated()")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Logout user")
  public ResponseEntity<?> logout(@AuthenticationPrincipal SecurityUser securityUser) {
    Logout operation = Logout.of(securityUser);
    return authService.logout(operation).process(logoutProcessor);
  }

  @PostMapping("recover-password")
  @Operation(summary = "Recover user password")
  public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordCommand recoverPassword) {
    Locale locale = LocaleContextHolder.getLocale();
    RecoverPassword operation = RecoverPassword.of(
        new UserEmail(recoverPassword.email()),
        new UserPasswordPassword(recoverPassword.newPassword()),
        locale
    );
    return authService.recoverPassword(operation).process(recoverPasswordProcessor);
  }

  @PostMapping("verify-recover-password")
  @Operation(summary = "Verify recover password code")
  public ResponseEntity<?> verifyRecoverPassword(
      @RequestBody VerifyRecoverPasswordCommand verifyRecoverPasswordCommand,
      HttpServletRequest request
  ) {
    SessionInfo sessionInfo = SessionInfo.of(request);

    VerifyRecoverPassword operation = VerifyRecoverPassword.of(
        new UserEmail(verifyRecoverPasswordCommand.email()),
        new VerificationCode(verifyRecoverPasswordCommand.recoverCode()),
        sessionInfo
    );
    return authService.verifyRecoverPassword(operation).process(verifyRecoverPasswordProcessor);
  }

  @PostMapping("force-register")
  @Operation(summary = "Force register new user (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> forceRegister(@RequestBody RegisterCommand command) {
    Locale locale = LocaleContextHolder.getLocale();
    ForceRegister operation = ForceRegister.of(
        new UserEmail(command.email()),
        new UserLogin(command.login(), new HashMap<>()),
        new UserPasswordPassword(command.password()),
        new UserRegion(command.region()),
        new UserPhoneNumber(command.phoneNumber()),
        new UserAvatar(command.avatarUrl()),
        locale
    );
    return authService.forceRegister(operation).process(forceRegisterProcessor);
  }

  @PostMapping("force-register-vendor")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Force register new vendor (Admin only)")
  public ResponseEntity<?> forceRegisterVendor(@RequestBody RegisterVendorCommand command) {
    Locale locale = LocaleContextHolder.getLocale();
    ForceRegisterVendor operation = ForceRegisterVendor.of(
        new UserEmail(StringUtils.trimToNull(command.email())),
        new UserLogin(StringUtils.trimToNull(command.login()), new HashMap<>()),
        new UserPasswordPassword(StringUtils.trimToNull(command.password())),
        new UserPhoneNumber(StringUtils.trimToNull(command.phoneNumber())),
        new VendorDetailsInn(StringUtils.trimToNull(command.inn())),
        new VendorDetailsAddress(StringUtils.trimToNull(command.address()), new HashMap<>()),
        command.countries() != null ?
            command.countries().stream().map(c -> new VendorCountryName(c, new HashMap<>()))
                .toList() : new ArrayList<>(),
        command.productCategories() != null ?
            command.productCategories().stream()
                .map(c -> new VendorProductCategoryName(c, new HashMap<>())).toList() :
            new ArrayList<>(),
        new UserAvatar(StringUtils.trimToNull(command.avatarUrl())),
        locale
    );
    return authService.forceRegisterVendor(operation).process(forceRegisterVendorProcessor);
  }
}
package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.RecoverPasswordRedisCacheManager;
import com.surofu.exporteru.application.cache.UserVerificationRedisCacheManager;
import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.auth.LoginSuccessDto;
import com.surofu.exporteru.application.dto.auth.RecoverPasswordDto;
import com.surofu.exporteru.application.dto.auth.RecoverPasswordSuccessDto;
import com.surofu.exporteru.application.dto.auth.VerifyEmailSuccessDto;
import com.surofu.exporteru.application.exception.CacheEntityNotFoundException;
import com.surofu.exporteru.application.exception.OutOfAttemptsException;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.application.utils.AuthUtils;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.session.SessionDeviceId;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserIsEnabled;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.model.user.password.UserPassword;
import com.surofu.exporteru.core.model.user.password.UserPasswordPassword;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsDescription;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.exporteru.core.repository.SessionRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.repository.UserPasswordRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.core.repository.VendorDetailsRepository;
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
import com.surofu.exporteru.core.service.mail.MailService;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService implements AuthService {
  private final UserRepository userRepository;
  private final UserPasswordRepository userPasswordRepository;
  private final TranslationRepository translationRepository;
  private final AuthenticationManager authenticationManager;
  private final VendorDetailsRepository vendorDetailsRepository;
  private final JwtUtils jwtUtils;
  private final UserVerificationRedisCacheManager userVerificationRedisCacheManager;
  private final RecoverPasswordRedisCacheManager recoverPasswordRedisCacheManager;
  private final PasswordEncoder passwordEncoder;
  private final MailService mailService;
  private final SessionRepository sessionRepository;

  @Value("${app.redis.verification-ttl-duration}")
  private Duration verificationTtl;

  @Value("${app.redis.recover-password-ttl-duration}")
  private Duration recoverPasswordTtl;

  @Override
  @Transactional
  public Register.Result register(Register operation) {
    if (userRepository.existsUserByEmail(operation.getUserEmail())) {
      return Register.Result.userWithEmailAlreadyExists(operation.getUserEmail());
    }

    if (operation.getUserPhoneNumber() != null &&
        userRepository.existsUserByPhoneNumber(operation.getUserPhoneNumber())) {
      return Register.Result.userWithPhoneNumberAlreadyExists(operation.getUserPhoneNumber());
    }

    User user = new User();
    user.setIsEnabled(UserIsEnabled.of(true));
    user.setRole(UserRole.ROLE_USER);
    user.setEmail(operation.getUserEmail());
    user.setLogin(setupUserLogin(operation.getUserLogin(), operation.getLocale()));
    user.setPhoneNumber(UserPhoneNumber.of(StringUtils.trimToNull(
        Objects.requireNonNullElse(operation.getUserPhoneNumber(), "").toString())));
    user.setRegion(operation.getUserRegion());
    user.setAvatar(operation.getAvatar());

    UserPassword userPassword = new UserPassword();
    userPassword.setUser(user);

    String rawHashedPassword =
        passwordEncoder.encode(operation.getUserPasswordPassword().getValue());
    UserPasswordPassword hashedUserPasswordPassword = UserPasswordPassword.of(rawHashedPassword);
    userPassword.setPassword(hashedUserPasswordPassword);

    String verificationCode = AuthUtils.generateVerificationCode();

    try {
      userVerificationRedisCacheManager.setUser(user.getEmail(), user);
      userVerificationRedisCacheManager.setUserPassword(user.getEmail(), userPassword);
      userVerificationRedisCacheManager.setVerificationCode(user.getEmail(), verificationCode);
    } catch (Exception e) {
      return Register.Result.saveInCacheError(e);
    }

    ZonedDateTime expiration = ZonedDateTime.now().plus(verificationTtl);

    try {
      mailService.sendVerificationMail(user.getEmail().toString(), verificationCode, expiration,
          operation.getLocale());
    } catch (Exception e) {
      return Register.Result.sendMailError(e);
    }

    return Register.Result.success(operation.getUserEmail());
  }

  @Override
  @Transactional
  public RegisterVendor.Result registerVendor(RegisterVendor operation) {
    if (userRepository.existsUserByEmail(operation.getUserEmail())) {
      return RegisterVendor.Result.userWithEmailAlreadyExists(operation.getUserEmail());
    }

    if (operation.getUserPhoneNumber() != null &&
        userRepository.existsUserByPhoneNumber(operation.getUserPhoneNumber())) {
      return RegisterVendor.Result.userWithPhoneNumberAlreadyExists(operation.getUserPhoneNumber());
    }

    if (vendorDetailsRepository.existsByInn(operation.getVendorDetailsInn())) {
      return RegisterVendor.Result.vendorWithInnAlreadyExists(operation.getVendorDetailsInn());
    }

    User user = new User();
    user.setRole(UserRole.ROLE_VENDOR);
    user.setIsEnabled(UserIsEnabled.of(true));
    user.setEmail(operation.getUserEmail());
    user.setLogin(setupUserLogin(operation.getUserLogin(), operation.getLocale()));
    user.setPhoneNumber(operation.getUserPhoneNumber());
    user.setRegion(operation.getUserRegion());
    user.setAvatar(operation.getAvatar());

    VendorDetails vendorDetails = new VendorDetails();
    vendorDetails.setInn(operation.getVendorDetailsInn());
    vendorDetails.setAddress(operation.getVendorDetailsAddress());
    VendorDetailsDescription description = VendorDetailsDescription.of("");
    description.setTranslations(new HashMap<>());
    vendorDetails.setDescription(description);

    List<VendorCountry> vendorCountries = new ArrayList<>();
    for (VendorCountryName vendorCountryName : operation.getVendorCountryNames()) {
      VendorCountry vendorCountry = new VendorCountry();
      vendorCountry.setVendorDetails(vendorDetails);
      vendorCountry.setName(vendorCountryName);

      try {
        vendorCountry.getName()
            .setTranslations(translationRepository.expand(vendorCountryName.toString()));
      } catch (Exception e) {
        return RegisterVendor.Result.translationError(e);
      }

      vendorCountries.add(vendorCountry);
    }

    List<VendorProductCategory> vendorProductCategories = new ArrayList<>();
    for (VendorProductCategoryName vendorProductCategoryName : operation.getVendorProductCategoryNames()) {
      VendorProductCategory vendorProductCategory = new VendorProductCategory();
      vendorProductCategory.setVendorDetails(vendorDetails);
      vendorProductCategory.setName(vendorProductCategoryName);

      try {
        vendorProductCategory.getName()
            .setTranslations(translationRepository.expand(vendorProductCategory.toString()));
      } catch (Exception e) {
        return RegisterVendor.Result.translationError(e);
      }

      vendorProductCategories.add(vendorProductCategory);
    }

    vendorDetails.setVendorCountries(new HashSet<>(vendorCountries));
    vendorDetails.setVendorProductCategories(new HashSet<>(vendorProductCategories));

    UserPassword userPassword = new UserPassword();
    userPassword.setUser(user);

    String rawHashedPassword =
        passwordEncoder.encode(operation.getUserPasswordPassword().getValue());
    UserPasswordPassword hashedUserPasswordPassword = UserPasswordPassword.of(rawHashedPassword);

    userPassword.setPassword(hashedUserPasswordPassword);

    user.setVendorDetails(vendorDetails);
    vendorDetails.setUser(user);

    String verificationCode = AuthUtils.generateVerificationCode();

    try {
      userVerificationRedisCacheManager.setUser(user.getEmail(), user);
      userVerificationRedisCacheManager.setUserPassword(user.getEmail(), userPassword);
      userVerificationRedisCacheManager.setVerificationCode(user.getEmail(), verificationCode);
    } catch (Exception e) {
      return RegisterVendor.Result.saveInCacheError(e);
    }

    ZonedDateTime expiration = ZonedDateTime.now().plus(verificationTtl);

    try {
      mailService.sendVerificationMail(user.getEmail().toString(), verificationCode, expiration,
          operation.getLocale());
    } catch (Exception e) {
      int retries = 3;

      while (retries > 0) {
        try {
          mailService.sendVerificationMail(user.getEmail().toString(), verificationCode, expiration,
              operation.getLocale());
          return RegisterVendor.Result.success(operation.getUserEmail());
        } catch (Exception ignored) {
          retries--;
        }
      }
      return RegisterVendor.Result.sendMailError(e);
    }

    return RegisterVendor.Result.success(operation.getUserEmail());
  }

  @Override
  @Transactional
  public LoginWithEmail.Result loginWithEmail(LoginWithEmail operation) {
    try {
      LoginSuccessDto dto = login(operation.getEmail(), operation.getPassword());
      return LoginWithEmail.Result.success(dto);
    } catch (DisabledException e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return LoginWithEmail.Result.accountBlocked(operation.getEmail());
    } catch (Exception ex) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return LoginWithEmail.Result.invalidCredentials();
    }
  }

  @Override
  @Transactional
  public VerifyEmail.Result verifyEmail(VerifyEmail operation) {
    String verificationCodeFromCache;

    try {
      verificationCodeFromCache =
          userVerificationRedisCacheManager.getVerificationCode(operation.getUserEmail());
    } catch (CacheEntityNotFoundException e) {
      return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
    } catch (OutOfAttemptsException e) {
      return VerifyEmail.Result.outOfAttempts(operation.getUserEmail());
    }

    if (!verificationCodeFromCache.equals(operation.getVerificationCode().toString())) {
      return VerifyEmail.Result.invalidVerificationCode(operation.getVerificationCode());
    }

    User user;

    try {
      user = userVerificationRedisCacheManager.getUser(operation.getUserEmail());
    } catch (CacheEntityNotFoundException e) {
      return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
    } catch (OutOfAttemptsException e) {
      return VerifyEmail.Result.outOfAttempts(operation.getUserEmail());
    }

    UserPassword userPassword =
        userVerificationRedisCacheManager.getUserPassword(operation.getUserEmail());

    if (userPassword == null) {
      return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
    }

    user.setPassword(userPassword);
    userPassword.setUser(user);

    if (user.getVendorDetails() != null) {
      VendorDetails vendorDetails = user.getVendorDetails();

      VendorDetailsDescription description = vendorDetails.getDescription();

      if (description == null) {
        vendorDetails.setDescription(VendorDetailsDescription.of(""));
      }

      if (description != null && description.getTranslations() == null) {
        description.setTranslations(new HashMap<>());
      }

      if (vendorDetails.getAddress() != null &&
          StringUtils.trimToNull(vendorDetails.getAddress().toString()) != null) {
        try {
          vendorDetails.getAddress().setTranslations(translationRepository
              .expand(vendorDetails.getAddress().toString()));
        } catch (IOException e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return VerifyEmail.Result.translationError(e);
        }
      }


    }

    SecurityUser securityUser = new SecurityUser(user, userPassword, operation.getSessionInfo());

    String accessToken = jwtUtils.generateAccessToken(securityUser);
    String refreshToken = jwtUtils.generateRefreshToken(securityUser);

    VerifyEmailSuccessDto verifyEmailSuccessDto =
        VerifyEmailSuccessDto.of(accessToken, refreshToken);

    try {
      userRepository.save(user);
    } catch (Exception e) {
      return VerifyEmail.Result.saveError(user.getEmail(), e);
    }

    SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
    Session oldSession = sessionRepository
        .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
        .orElse(new Session());
    Session session = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);

    try {
      sessionRepository.save(session);
    } catch (Exception e) {
      return VerifyEmail.Result.saveSessionError(user.getEmail(), e);
    }

    userVerificationRedisCacheManager.clearCache(user.getEmail());
    return VerifyEmail.Result.success(verifyEmailSuccessDto);
  }

  @Override
  @Transactional
  public Logout.Result logout(Logout operation) {
    SessionInfo sessionInfo = operation.getSecurityUser().getSessionInfo();
    Long userId = operation.getSecurityUser().getUser().getId();
    SessionDeviceId sessionDeviceId = sessionInfo.getDeviceId();

    try {
      sessionRepository.deleteSessionByUserIdAndDeviceId(userId, sessionDeviceId);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return Logout.Result.deleteError(e);
    }

    return Logout.Result.success();
  }

  @Override
  @Transactional(readOnly = true)
  public RecoverPassword.Result recoverPassword(RecoverPassword operation) {
    if (!userRepository.existsUserByEmail(operation.getUserEmail())) {
      return RecoverPassword.Result.userNotFound(operation.getUserEmail());
    }

    String recoverCode = AuthUtils.generateVerificationCode();
    ZonedDateTime expiration = ZonedDateTime.now().plus(recoverPasswordTtl);

    RecoverPasswordDto recoverPasswordDto =
        new RecoverPasswordDto(recoverCode, operation.getNewUserPassword());
    recoverPasswordRedisCacheManager.set(operation.getUserEmail(), recoverPasswordDto);

    try {
      mailService.sendRecoverPasswordVerificationMail(operation.getUserEmail().toString(),
          recoverCode, expiration, operation.getLocale());
    } catch (Exception e) {
      return RecoverPassword.Result.sendMailError(operation.getUserEmail(), e);
    }

    return RecoverPassword.Result.success(operation.getUserEmail());
  }

  @Override
  @Transactional
  public VerifyRecoverPassword.Result verifyRecoverPassword(VerifyRecoverPassword operation) {
    RecoverPasswordDto recoverPasswordDto =
        recoverPasswordRedisCacheManager.get(operation.getUserEmail());

    if (recoverPasswordDto == null) {
      return VerifyRecoverPassword.Result.emailNotFound(operation.getUserEmail());
    }

    if (!recoverPasswordDto.recoverCode().equals(operation.getRecoverCode().toString())) {
      return VerifyRecoverPassword.Result.invalidRecoverCode(operation.getUserEmail(),
          recoverPasswordDto.recoverCode());
    }

    Optional<UserPassword> userPassword =
        userPasswordRepository.getUserPasswordByUserEmailWithUser(operation.getUserEmail());

    if (userPassword.isEmpty()) {
      return VerifyRecoverPassword.Result.userNotFound(operation.getUserEmail());
    }

    String hashedRawPassword =
        passwordEncoder.encode(recoverPasswordDto.newUserPassword().getValue());
    UserPasswordPassword hashedUserPassword = UserPasswordPassword.of(hashedRawPassword);
    userPassword.get().setPassword(hashedUserPassword);

    SecurityUser securityUser = new SecurityUser(
        userPassword.get().getUser(),
        userPassword.get(),
        operation.getSessionInfo()
    );

    String accessToken = jwtUtils.generateAccessToken(securityUser);
    String refreshToken = jwtUtils.generateRefreshToken(securityUser);

    RecoverPasswordSuccessDto recoverPasswordSuccessDto =
        RecoverPasswordSuccessDto.of(accessToken, refreshToken);

    try {
      userPasswordRepository.saveUserPassword(userPassword.get());
      recoverPasswordRedisCacheManager.clear(userPassword.get().getUser().getEmail());
    } catch (Exception e) {
      log.error("Error saving user password: {}", e.getMessage(), e);
      return VerifyRecoverPassword.Result.saveError(e);
    }

    SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
    Session oldSession = sessionRepository
        .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
        .orElse(new Session());
    Session session = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);

    try {
      sessionRepository.save(session);
    } catch (Exception e) {
      log.error("Save session error: {}", e.getMessage());
      throw e;
    }

    return VerifyRecoverPassword.Result.success(recoverPasswordSuccessDto,
        operation.getUserEmail());
  }

  @Override
  @Transactional
  public ForceRegister.Result forceRegister(ForceRegister operation) {
    if (userRepository.existsUserByEmail(operation.getEmail())) {
      return ForceRegister.Result.userWithEmailAlreadyExists(operation.getEmail());
    }

    if (userRepository.existsUserByLogin(operation.getLogin())) {
      return ForceRegister.Result.userWithLoginAlreadyExists(operation.getLogin());
    }

    if (userRepository.existsUserByPhoneNumber(operation.getPhoneNumber())) {
      return ForceRegister.Result.userWithPhoneNumberAlreadyExists(operation.getPhoneNumber());
    }

    User user = new User();
    user.setRole(UserRole.ROLE_USER);
    user.setIsEnabled(UserIsEnabled.of(true));
    user.setEmail(operation.getEmail());
    user.setLogin(setupUserLogin(operation.getLogin(), operation.getLocale()));
    user.setPhoneNumber(operation.getPhoneNumber());
    user.setRegion(operation.getRegion());
    user.setAvatar(operation.getAvatar());

    UserPassword userPassword = new UserPassword();
    userPassword.setUser(user);
    user.setPassword(userPassword);

    String passwordHash = passwordEncoder.encode(operation.getPassword().toString());
    userPassword.setPassword(UserPasswordPassword.of(passwordHash));

    try {
      userRepository.save(user);
      return ForceRegister.Result.success(operation.getEmail());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ForceRegister.Result.saveError(e);
    }
  }

  @Override
  @Transactional
  public ForceRegisterVendor.Result forceRegisterVendor(ForceRegisterVendor operation) {
    // Validation
    if (operation.getCountryNames().isEmpty()) {
      return ForceRegisterVendor.Result.emptyVendorCountries();
    }

    if (operation.getProductCategoryNames().isEmpty()) {
      return ForceRegisterVendor.Result.emptyVendorProductCategories();
    }

    if (userRepository.existsUserByEmail(operation.getEmail())) {
      return ForceRegisterVendor.Result.vendorWithEmailAlreadyExists(operation.getEmail());
    }

    if (userRepository.existsUserByLogin(operation.getLogin())) {
      return ForceRegisterVendor.Result.vendorWithLoginAlreadyExists(operation.getLogin());
    }

    if (userRepository.existsUserByPhoneNumber(operation.getPhoneNumber())) {
      return ForceRegisterVendor.Result.vendorWithPhoneNumberAlreadyExists(
          operation.getPhoneNumber());
    }

    if (vendorDetailsRepository.existsByInn(operation.getInn())) {
      return ForceRegisterVendor.Result.vendorWithInnAlreadyExists(operation.getInn());
    }

    // Setting
    User user = new User();
    user.setRole(UserRole.ROLE_VENDOR);
    user.setIsEnabled(UserIsEnabled.of(true));
    user.setEmail(operation.getEmail());
    user.setLogin(setupUserLogin(operation.getLogin(), operation.getLocale()));
    user.setPhoneNumber(operation.getPhoneNumber());
    user.setAvatar(operation.getAvatar());

    UserPassword userPassword = new UserPassword();
    userPassword.setUser(user);
    user.setPassword(userPassword);
    String passwordHash = passwordEncoder.encode(operation.getPassword().toString());
    userPassword.setPassword(UserPasswordPassword.of(passwordHash));

    VendorDetails vendorDetails = new VendorDetails();
    vendorDetails.setUser(user);
    user.setVendorDetails(vendorDetails);
    vendorDetails.setInn(operation.getInn());
    vendorDetails.setAddress(operation.getAddress());

    List<VendorCountry> vendorCountryList = new ArrayList<>();

    for (VendorCountryName name : operation.getCountryNames()) {
      VendorCountry country = new VendorCountry();
      country.setVendorDetails(vendorDetails);
      country.setName(name);

      try {
        country.getName().setTranslations(translationRepository.expand(name.toString()));
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ForceRegisterVendor.Result.translationError(e);
      }

      vendorCountryList.add(country);
    }

    List<VendorProductCategory> vendorProductCategoryList = new ArrayList<>();

    for (VendorProductCategoryName name : operation.getProductCategoryNames()) {
      VendorProductCategory productCategory = new VendorProductCategory();
      productCategory.setVendorDetails(vendorDetails);
      productCategory.setName(name);

      try {
        productCategory.getName().setTranslations(translationRepository.expand(name.toString()));
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ForceRegisterVendor.Result.translationError(e);
      }

      vendorProductCategoryList.add(productCategory);
    }

    // Setting Translated
    vendorDetails.setVendorCountries(new HashSet<>(vendorCountryList));
    vendorDetails.setVendorProductCategories(new HashSet<>(vendorProductCategoryList));

    // Save
    try {
      userRepository.save(user);
      return ForceRegisterVendor.Result.success(operation.getEmail());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ForceRegisterVendor.Result.saveError(e);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected LoginSuccessDto login(UserEmail userEmail, UserPasswordPassword userPasswordPassword)
      throws AuthenticationException {
    Authentication authenticationRequest =
        new UsernamePasswordAuthenticationToken(userEmail.toString(),
            userPasswordPassword.toString());
    Authentication authenticationResponse =
        authenticationManager.authenticate(authenticationRequest);

    SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
    SecurityUser securityUser = (SecurityUser) authenticationResponse.getPrincipal();

    String accessToken = jwtUtils.generateAccessToken(securityUser);
    String refreshToken = jwtUtils.generateRefreshToken(securityUser);

    SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
    Session oldSession = sessionRepository
        .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
        .orElse(new Session());
    Session session = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);

    try {
      sessionRepository.save(session);
    } catch (Exception e) {
      log.error("Save session error: {}", e.getMessage());
      throw e;
    }
    return LoginSuccessDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  @Transactional
  public LoginWithTelegram.Result loginWithTelegram(LoginWithTelegram operation) {
    if (operation.getTelegramUser().id() == null) {
      return LoginWithTelegram.Result.failure(operation.getTelegramUser().firstName());
    }

    Optional<User> userOptional =
        userRepository.getUserByTelegramUserId(operation.getTelegramUser().id());

    if (userOptional.isEmpty()) {
      return LoginWithTelegram.Result.failure(operation.getTelegramUser().firstName());
    }

    User user = userOptional.get();

    Session session = sessionRepository.getSessionByUserIdAndDeviceId(user.getId(),
            operation.getSessionInfo().getDeviceId())
        .orElse(Session.of(operation.getSessionInfo(), user, new Session()));

    try {
      sessionRepository.save(session);
    } catch (Exception e) {
      log.error("Save session error: {}", e.getMessage());
      return LoginWithTelegram.Result.failure(operation.getTelegramUser().firstName());
    }

    SecurityUser securityUser =
        new SecurityUser(user, user.getPassword(), operation.getSessionInfo());

    String accessToken = jwtUtils.generateAccessToken(securityUser);
    String refreshToken = jwtUtils.generateRefreshToken(securityUser);

    LoginSuccessDto dto = LoginSuccessDto.of(accessToken, refreshToken);
    return LoginWithTelegram.Result.success(dto, operation.getTelegramUser().firstName());
  }

  private UserLogin setupUserLogin(UserLogin userLogin, Locale locale) {
    String rawUserLogin = userLogin.toString();
    String transliterationLogin = TransliterationManager.transliterate(rawUserLogin);

    switch (locale.getLanguage()) {
      case "ru" -> userLogin.setTransliteration(Map.of(
          "en", transliterationLogin,
          "ru", rawUserLogin,
          "zh", transliterationLogin,
          "hi", transliterationLogin
      ));
      case "zh" -> userLogin.setTransliteration(Map.of(
          "en", transliterationLogin,
          "ru", transliterationLogin,
          "zh", rawUserLogin,
          "hi", transliterationLogin
      ));
      case "hi" -> userLogin.setTransliteration(Map.of(
          "en", transliterationLogin,
          "ru", transliterationLogin,
          "zh", transliterationLogin,
          "hi", rawUserLogin
      ));
      default -> userLogin.setTransliteration(Map.of(
          "en", transliterationLogin,
          "ru", transliterationLogin,
          "zh", transliterationLogin,
          "hi", transliterationLogin
      ));
    }

    return userLogin;
  }
}

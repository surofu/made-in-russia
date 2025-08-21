package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.cache.RecoverPasswordRedisCacheManager;
import com.surofu.madeinrussia.application.cache.UserVerificationRedisCacheManager;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.auth.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.auth.RecoverPasswordDto;
import com.surofu.madeinrussia.application.dto.auth.RecoverPasswordSuccessDto;
import com.surofu.madeinrussia.application.dto.auth.VerifyEmailSuccessDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.AuthUtils;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.repository.TranslationRepository;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.repository.VendorDetailsRepository;
import com.surofu.madeinrussia.core.service.auth.AuthService;
import com.surofu.madeinrussia.core.service.auth.operation.*;
import com.surofu.madeinrussia.core.service.mail.MailService;
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
    private final AsyncAuthApplicationService asyncAuthApplicationService;
    private final AsyncSessionApplicationService asyncSessionApplicationService;
    private final UserVerificationRedisCacheManager userVerificationRedisCacheManager;
    private final RecoverPasswordRedisCacheManager recoverPasswordRedisCacheManager;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
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

        if (userRepository.existsUserByLogin(operation.getUserLogin())) {
            return Register.Result.userWithLoginAlreadyExists(operation.getUserLogin());
        }

        if (operation.getUserPhoneNumber() != null && userRepository.existsUserByPhoneNumber(operation.getUserPhoneNumber())) {
            return Register.Result.userWithPhoneNumberAlreadyExists(operation.getUserPhoneNumber());
        }

        User user = new User();
        user.setIsEnabled(UserIsEnabled.of(true));
        user.setRole(UserRole.ROLE_USER);
        user.setEmail(operation.getUserEmail());
        user.setLogin(operation.getUserLogin());
        user.setPhoneNumber(operation.getUserPhoneNumber());
        user.setRegion(operation.getUserRegion());
        user.setAvatar(operation.getAvatar());

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);

        String rawHashedPassword = passwordEncoder.encode(operation.getUserPasswordPassword().getValue());
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

        LocalDateTime expiration = LocalDateTime.now().plus(verificationTtl);

        try {
            mailService.sendVerificationMail(user.getEmail().toString(), verificationCode, expiration, operation.getLocale());
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

        if (userRepository.existsUserByLogin(operation.getUserLogin())) {
            return RegisterVendor.Result.userWithLoginAlreadyExists(operation.getUserLogin());
        }

        if (operation.getUserPhoneNumber() != null && userRepository.existsUserByPhoneNumber(operation.getUserPhoneNumber())) {
            return RegisterVendor.Result.userWithPhoneNumberAlreadyExists(operation.getUserPhoneNumber());
        }

        if (vendorDetailsRepository.existsByInn(operation.getVendorDetailsInn())) {
            return RegisterVendor.Result.vendorWithInnAlreadyExists(operation.getVendorDetailsInn());
        }

        User user = new User();
        user.setRole(UserRole.ROLE_VENDOR);
        user.setIsEnabled(UserIsEnabled.of(true));
        user.setEmail(operation.getUserEmail());
        user.setLogin(operation.getUserLogin());
        user.setPhoneNumber(operation.getUserPhoneNumber());
        user.setRegion(operation.getUserRegion());
        user.setAvatar(operation.getAvatar());

        VendorDetails vendorDetails = new VendorDetails();
        vendorDetails.setInn(operation.getVendorDetailsInn());

        Map<String, List<String>> translationMap = new HashMap<>();

        List<VendorCountry> vendorCountries = new ArrayList<>();
        for (VendorCountryName vendorCountryName : operation.getVendorCountryNames()) {
            VendorCountry vendorCountry = new VendorCountry();
            vendorCountry.setVendorDetails(vendorDetails);
            vendorCountry.setName(vendorCountryName);
            vendorCountries.add(vendorCountry);
            translationMap.computeIfAbsent("VendorCountryName", k -> new ArrayList<>()).add(vendorCountryName.toString());
        }

        List<VendorProductCategory> vendorProductCategories = new ArrayList<>();
        for (VendorProductCategoryName vendorProductCategoryName : operation.getVendorProductCategoryNames()) {
            VendorProductCategory vendorProductCategory = new VendorProductCategory();
            vendorProductCategory.setVendorDetails(vendorDetails);
            vendorProductCategory.setName(vendorProductCategoryName);
            vendorProductCategories.add(vendorProductCategory);
            translationMap.computeIfAbsent("VendorProductCategoryName", k -> new ArrayList<>()).add(vendorProductCategoryName.toString());
        }

        Map<String, List<HstoreTranslationDto>> translationResult = null;
        try {
            translationResult = translationRepository.expandStrings(translationMap);
        } catch (Exception e) {
            int retries = 3;
            boolean isSuccess = false;

            while (retries > 0) {
                try {
                    translationResult = translationRepository.expandStrings(translationMap);
                    isSuccess = true;
                    break;
                } catch (Exception ignored) {
                }
                retries--;
            }

            if (!isSuccess) {
                return RegisterVendor.Result.translationError(e);
            }
        }

        if (translationResult == null) {
            return RegisterVendor.Result.translationError(new Exception("Translation result is null"));
        }

        for (int i = 0; i < vendorCountries.size(); i++) {
            VendorCountry country = vendorCountries.get(i);
            country.getName().setTranslations(translationResult.get("VendorCountryName").get(i));
        }

        for (int i = 0; i < vendorProductCategories.size(); i++) {
            VendorProductCategory country = vendorProductCategories.get(i);
            country.getName().setTranslations(translationResult.get("VendorProductCategoryName").get(i));
        }

        vendorDetails.setVendorCountries(new HashSet<>(vendorCountries));
        vendorDetails.setVendorProductCategories(new HashSet<>(vendorProductCategories));

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);

        String rawHashedPassword = passwordEncoder.encode(operation.getUserPasswordPassword().getValue());
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

        LocalDateTime expiration = LocalDateTime.now().plus(verificationTtl);

        try {
            mailService.sendVerificationMail(user.getEmail().toString(), verificationCode, expiration, operation.getLocale());
        } catch (Exception e) {
            int retries = 3;

            while (retries > 0) {
                try {
                    mailService.sendVerificationMail(user.getEmail().toString(), verificationCode, expiration, operation.getLocale());
                    return RegisterVendor.Result.success(operation.getUserEmail());
                } catch (Exception ignored) {
                    retries--;
                }
            }

            log.info("Used retries to send verification code mail: {}", 3 - retries);
            return RegisterVendor.Result.sendMailError(e);
        }

        return RegisterVendor.Result.success(operation.getUserEmail());
    }

    @Override
    public LoginWithEmail.Result loginWithEmail(LoginWithEmail operation) {
        try {
            LoginSuccessDto dto = login(operation.getEmail(), operation.getPassword());
            return LoginWithEmail.Result.success(dto);
        } catch (DisabledException e) {
            return LoginWithEmail.Result.accountBlocked(operation.getEmail());
        } catch (Exception ex) {
            return LoginWithEmail.Result.invalidCredentials(operation.getEmail(), operation.getPassword());
        }
    }

    @Override
    public LoginWithLogin.Result loginWithLogin(LoginWithLogin operation) {
        Optional<UserEmail> userEmail = userRepository.getUserEmailByLogin(operation.getLogin());

        if (userEmail.isEmpty()) {
            return LoginWithLogin.Result.invalidCredentials(operation.getLogin(), operation.getPassword());
        }

        try {
            LoginSuccessDto dto = login(userEmail.get(), operation.getPassword());
            return LoginWithLogin.Result.success(dto);
        } catch (DisabledException e) {
            return LoginWithLogin.Result.accountBlocked(operation.getLogin());
        } catch (Exception e) {
            return LoginWithLogin.Result.invalidCredentials(operation.getLogin(), operation.getPassword());
        }
    }

    @Override
    @Transactional
    public VerifyEmail.Result verifyEmail(VerifyEmail operation) {
        String verificationCodeFromCache = userVerificationRedisCacheManager.getVerificationCode(operation.getUserEmail());

        if (verificationCodeFromCache == null) {
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        if (!verificationCodeFromCache.equals(operation.getVerificationCode().toString())) {
            return VerifyEmail.Result.invalidVerificationCode(operation.getVerificationCode());
        }

        User user = userVerificationRedisCacheManager.getUser(operation.getUserEmail());

        if (user == null) {
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        UserPassword userPassword = userVerificationRedisCacheManager.getUserPassword(operation.getUserEmail());

        if (userPassword == null) {
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        user.setPassword(userPassword);
        userPassword.setUser(user);

        SecurityUser securityUser = new SecurityUser(user, userPassword, operation.getSessionInfo());

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        VerifyEmailSuccessDto verifyEmailSuccessDto = VerifyEmailSuccessDto.of(accessToken, refreshToken);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            return VerifyEmail.Result.saveError(e);
        } finally {
            userVerificationRedisCacheManager.clearCache(user.getEmail());
        }

        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser)
                .exceptionally(ex -> {
                    log.error("Error while saving session", ex);
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

    @Override
    @Transactional
    public RecoverPassword.Result recoverPassword(RecoverPassword operation) {
        boolean isUserExists = userRepository.existsUserByEmail(operation.getUserEmail());

        if (!isUserExists) {
            return RecoverPassword.Result.userNotFound(operation.getUserEmail());
        }

        String recoverCode = AuthUtils.generateVerificationCode();
        LocalDateTime expiration = LocalDateTime.now().plus(recoverPasswordTtl);

        RecoverPasswordDto recoverPasswordDto = new RecoverPasswordDto(recoverCode, operation.getNewUserPassword());
        recoverPasswordRedisCacheManager.set(operation.getUserEmail(), recoverPasswordDto);

        try {
            mailService.sendRecoverPasswordVerificationMail(operation.getUserEmail().toString(), recoverCode, expiration);
        } catch (Exception e) {
            return RecoverPassword.Result.sendMailError(operation.getUserEmail(), e);
        }

        return RecoverPassword.Result.success(operation.getUserEmail());
    }

    @Override
    @Transactional
    public VerifyRecoverPassword.Result verifyRecoverPassword(VerifyRecoverPassword operation) {
        RecoverPasswordDto recoverPasswordDto = recoverPasswordRedisCacheManager.get(operation.getUserEmail());

        if (recoverPasswordDto == null) {
            return VerifyRecoverPassword.Result.emailNotFound(operation.getUserEmail());
        }

        if (!recoverPasswordDto.recoverCode().equals(operation.getRecoverCode().toString())) {
            return VerifyRecoverPassword.Result.invalidRecoverCode(operation.getUserEmail(), recoverPasswordDto.recoverCode());
        }

        Optional<UserPassword> userPassword = userPasswordRepository.getUserPasswordByUserEmailWithUser(operation.getUserEmail());

        if (userPassword.isEmpty()) {
            return VerifyRecoverPassword.Result.userNotFound(operation.getUserEmail());
        }

        String hashedRawPassword = passwordEncoder.encode(recoverPasswordDto.newUserPassword().getValue());
        UserPasswordPassword hashedUserPassword = UserPasswordPassword.of(hashedRawPassword);
        userPassword.get().setPassword(hashedUserPassword);

        SecurityUser securityUser = new SecurityUser(
                userPassword.get().getUser(),
                userPassword.get(),
                operation.getSessionInfo()
        );

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        String refreshToken = jwtUtils.generateRefreshToken(securityUser);

        RecoverPasswordSuccessDto recoverPasswordSuccessDto = RecoverPasswordSuccessDto.of(accessToken, refreshToken);

        asyncAuthApplicationService.saveUserPasswordInDatabaseAndClearRecoverPasswordCacheByUserEmail(userPassword.get(), operation.getUserEmail());
        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser);

        return VerifyRecoverPassword.Result.success(recoverPasswordSuccessDto, operation.getUserEmail());
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
        user.setLogin(operation.getLogin());
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
            return ForceRegisterVendor.Result.vendorWithPhoneNumberAlreadyExists(operation.getPhoneNumber());
        }

        if (vendorDetailsRepository.existsByInn(operation.getInn())) {
            return ForceRegisterVendor.Result.vendorWithInnAlreadyExists(operation.getInn());
        }

        // Setting
        User user = new User();
        user.setRole(UserRole.ROLE_VENDOR);
        user.setIsEnabled(UserIsEnabled.of(true));
        user.setEmail(operation.getEmail());
        user.setLogin(operation.getLogin());
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

        List<VendorCountry> vendorCountryList = new ArrayList<>();

        for (VendorCountryName name : operation.getCountryNames()) {
            VendorCountry country = new VendorCountry();
            country.setVendorDetails(vendorDetails);
            country.setName(name);
            vendorCountryList.add(country);
        }

        List<VendorProductCategory> vendorProductCategoryList = new ArrayList<>();

        for (VendorProductCategoryName name : operation.getProductCategoryNames()) {
            VendorProductCategory productCategory = new VendorProductCategory();
            productCategory.setVendorDetails(vendorDetails);
            productCategory.setName(name);
            vendorProductCategoryList.add(productCategory);
        }

        // Translation
        List<String> vendorCountryNameList = vendorCountryList.stream().map(VendorCountry::getName).map(VendorCountryName::toString).toList();
        List<String> vendorProductCategoryNameList = vendorProductCategoryList.stream().map(VendorProductCategory::getName).map(VendorProductCategoryName::toString).toList();

        List<String> allNameList = new ArrayList<>(vendorCountryNameList.size() + vendorProductCategoryNameList.size());
        allNameList.addAll(vendorCountryNameList);
        allNameList.addAll(vendorProductCategoryNameList);

        String[] stringArray = allNameList.toArray(new String[0]);

        TranslationResponse responseEn, responseRu, responseZh;

        try {
            responseEn = translationRepository.translateToEn(stringArray);
            responseRu = translationRepository.translateToRu(stringArray);
            responseZh = translationRepository.translateToZh(stringArray);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceRegisterVendor.Result.translationError(e);
        }

        for (int i = 0; i < allNameList.size(); i++) {
            if (vendorCountryList.size() > i) {
                if (i == 0) {
                    user.setRegion(UserRegion.of(responseEn.getTranslations()[i].getText()));
                }

                vendorCountryList.get(i).getName().setTranslations(new HstoreTranslationDto(
                        responseEn.getTranslations()[i].getText(),
                        responseRu.getTranslations()[i].getText(),
                        responseZh.getTranslations()[i].getText()
                ));
            }

            if (vendorCountryList.size() <= i) {
                vendorProductCategoryList.get(i - vendorCountryList.size()).getName().setTranslations(new HstoreTranslationDto(
                        responseEn.getTranslations()[i].getText(),
                        responseRu.getTranslations()[i].getText(),
                        responseZh.getTranslations()[i].getText()
                ));
            }
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

    protected LoginSuccessDto login(UserEmail userEmail, UserPasswordPassword userPasswordPassword) throws AuthenticationException {
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

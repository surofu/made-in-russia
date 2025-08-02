package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.application.dto.auth.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.auth.RecoverPasswordDto;
import com.surofu.madeinrussia.application.dto.auth.RecoverPasswordSuccessDto;
import com.surofu.madeinrussia.application.dto.auth.VerifyEmailSuccessDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncAuthApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.application.utils.RecoverPasswordCaffeineCacheManager;
import com.surofu.madeinrussia.application.utils.UserVerificationCaffeineCacheManager;
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
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final UserVerificationCaffeineCacheManager userVerificationCaffeineCacheManager;
    private final RecoverPasswordCaffeineCacheManager recoverPasswordCaffeineCacheManager;
    private final PasswordEncoder passwordEncoder;

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

        asyncAuthApplicationService.saveRegisterDataInCacheAndSendVerificationCodeToEmail(operation)
                .exceptionally(ex -> {
                    log.error("Error while saving register code", ex);
                    return null;
                });

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

        if (userRepository.existsUserByPhoneNumber(operation.getUserPhoneNumber())) {
            return RegisterVendor.Result.userWithPhoneNumberAlreadyExists(operation.getUserPhoneNumber());
        }

        if (vendorDetailsRepository.existsByInn(operation.getVendorDetailsInn())) {
            return RegisterVendor.Result.vendorWithInnAlreadyExists(operation.getVendorDetailsInn());
        }

        asyncAuthApplicationService.saveRegisterVendorDataInCacheAndSendVerificationCodeToEmail(operation)
                .exceptionally(ex -> {
                    log.error("Error while saving register code", ex);
                    return null;
                });

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
        String verificationCodeFromCache = userVerificationCaffeineCacheManager.getVerificationCode(operation.getUserEmail());

        if (verificationCodeFromCache == null) {
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        if (!verificationCodeFromCache.equals(operation.getVerificationCode().toString())) {
            return VerifyEmail.Result.invalidVerificationCode(operation.getVerificationCode());
        }

        User user = userVerificationCaffeineCacheManager.getUser(operation.getUserEmail());

        if (user == null) {
            log.error("User with email {} not found in cache", operation.getUserEmail());
            return VerifyEmail.Result.accountNotFound(operation.getUserEmail());
        }

        if (user.getVendorDetails() != null) {
            List<String> vendorCountryNames = user.getVendorDetails().getVendorCountries().stream()
                    .map(VendorCountry::getName).map(VendorCountryName::toString).toList();
            List<String> vendorPcNames = user.getVendorDetails().getVendorProductCategories().stream()
                    .map(VendorProductCategory::getName).map(VendorProductCategoryName::toString).toList();

            List<String> vendorStrings = new ArrayList<>(vendorCountryNames.size() + vendorPcNames.size());

            vendorStrings.addAll(vendorCountryNames);
            vendorStrings.addAll(vendorPcNames);

            try {
                TranslationResponse enTranslationResponse = translationRepository.translateToEn(vendorStrings.toArray(new String[0]));
                TranslationResponse ruTranslationResponse = translationRepository.translateToRu(vendorStrings.toArray(new String[0]));
                TranslationResponse zhTranslationResponse = translationRepository.translateToZh(vendorStrings.toArray(new String[0]));

                for (int i = 0; i < vendorCountryNames.size(); i++) {
                    VendorCountry vendorCountry = user.getVendorDetails().getVendorCountries()
                            .toArray(VendorCountry[]::new)[i];
                    vendorCountry.getName().setTranslations(new HstoreTranslationDto(
                            enTranslationResponse.getTranslations()[i].getText(),
                            ruTranslationResponse.getTranslations()[i].getText(),
                            zhTranslationResponse.getTranslations()[i].getText()
                    ));

                    VendorProductCategory vendorProductCategory = user.getVendorDetails().getVendorProductCategories()
                            .toArray(VendorProductCategory[]::new)[i];
                    vendorProductCategory.getName().setTranslations(new HstoreTranslationDto(
                       enTranslationResponse.getTranslations()[vendorCountryNames.size() + i].getText(),
                       ruTranslationResponse.getTranslations()[vendorCountryNames.size() + i].getText(),
                       zhTranslationResponse.getTranslations()[vendorCountryNames.size() + i].getText()
                    ));
                }
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return VerifyEmail.Result.translationError(e);
            }
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

    @Override
    @Transactional
    public RecoverPassword.Result recoverPassword(RecoverPassword operation) {
        boolean isUserExists = userRepository.existsUserByEmail(operation.getUserEmail());

        if (isUserExists) {
            RecoverPassword operationWithHashedPassword = RecoverPassword.of(
                    operation.getUserEmail(),
                    operation.getNewUserPassword()
            );

            asyncAuthApplicationService.saveRecoverPasswordDataInCacheAndSendRecoverCodeToEmail(operationWithHashedPassword);
            return RecoverPassword.Result.success(operation.getUserEmail());
        }

        return RecoverPassword.Result.userNotFound(operation.getUserEmail());
    }

    @Override
    @Transactional
    public VerifyRecoverPassword.Result verifyRecoverPassword(VerifyRecoverPassword operation) {
        RecoverPasswordDto recoverPasswordDto = recoverPasswordCaffeineCacheManager.getRecoverPasswordDto(operation.getUserEmail());

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

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);
        user.setPassword(userPassword);

        String passwordHash = passwordEncoder.encode(operation.getPassword().toString());
        userPassword.setPassword(UserPasswordPassword.of(passwordHash));

        try {
            userRepository.saveUser(user);
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
            userRepository.saveUser(user);
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

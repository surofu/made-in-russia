package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.application.dto.RecoverPasswordDto;
import com.surofu.madeinrussia.application.utils.RecoverPasswordCaffeineCacheManager;
import com.surofu.madeinrussia.application.utils.UserVerificationCaffeineCacheManager;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.model.userPassword.UserPasswordPassword;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.auth.operation.RecoverPassword;
import com.surofu.madeinrussia.core.service.auth.operation.Register;
import com.surofu.madeinrussia.core.service.auth.operation.RegisterVendor;
import com.surofu.madeinrussia.core.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAuthApplicationService {
    private final UserRepository userRepository;
    private final UserPasswordRepository passwordRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final UserVerificationCaffeineCacheManager userVerificationCaffeineCacheManager;
    private final RecoverPasswordCaffeineCacheManager recoverPasswordCaffeineCacheManager;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> saveRegisterDataInCacheAndSendVerificationCodeToEmail(Register operation) throws CompletionException {
        User user = new User();
        user.setRole(UserRole.ROLE_USER);
        user.setEmail(operation.getUserEmail());
        user.setLogin(operation.getUserLogin());
        user.setPhoneNumber(operation.getUserPhoneNumber());
        user.setRegion(operation.getUserRegion());

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);

        String rawHashedPassword = passwordEncoder.encode(operation.getUserPasswordPassword().getValue());
        UserPasswordPassword hashedUserPasswordPassword = UserPasswordPassword.of(rawHashedPassword);
        userPassword.setPassword(hashedUserPasswordPassword);

        return saveUserInCacheAndSendMessage(user, userPassword);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> saveRegisterVendorDataInCacheAndSendVerificationCodeToEmail(RegisterVendor operation) throws CompletionException {
        User user = new User();
        user.setRole(UserRole.ROLE_VENDOR);
        user.setEmail(operation.getUserEmail());
        user.setLogin(operation.getUserLogin());
        user.setPhoneNumber(operation.getUserPhoneNumber());
        user.setRegion(operation.getUserRegion());

        VendorDetails vendorDetails = new VendorDetails();
        vendorDetails.setInn(operation.getVendorDetailsInn());
//        vendorDetails.setPaymentDetails(operation.getVendorDetailsPaymentDetails());

        Set<VendorCountry> vendorCountries = new HashSet<>();

        for (VendorCountryName vendorCountryName : operation.getVendorCountryNames()) {
            VendorCountry vendorCountry = new VendorCountry();
            vendorCountry.setVendorDetails(vendorDetails);
            vendorCountry.setName(vendorCountryName);
            vendorCountries.add(vendorCountry);
        }

        vendorDetails.setVendorCountries(vendorCountries);

        Set<VendorProductCategory> vendorProductCategories = new HashSet<>();

        for (VendorProductCategoryName vendorProductCategoryName : operation.getVendorProductCategoryNames()) {
            VendorProductCategory vendorProductCategory = new VendorProductCategory();
            vendorProductCategory.setVendorDetails(vendorDetails);
            vendorProductCategory.setName(vendorProductCategoryName);
            vendorProductCategories.add(vendorProductCategory);
        }

        vendorDetails.setVendorProductCategories(vendorProductCategories);

        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);

        String rawHashedPassword = passwordEncoder.encode(operation.getUserPasswordPassword().getValue());
        UserPasswordPassword hashedUserPasswordPassword = UserPasswordPassword.of(rawHashedPassword);

        userPassword.setPassword(hashedUserPasswordPassword);

        user.setVendorDetails(vendorDetails);
        vendorDetails.setUser(user);

        return saveUserInCacheAndSendMessage(user, userPassword);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> saveUserInDatabaseAndRemoveFromCache(User user, UserPassword userPassword) throws CompletionException {
        try {
            userRepository.saveUser(user);
            passwordRepository.saveUserPassword(userPassword);
            userVerificationCaffeineCacheManager.clearCache(user.getEmail());
        } catch (Exception ex) {
            log.error("Error saving user or password: {}", ex.getMessage(), ex);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRecoverPasswordDataInCacheAndSendRecoverCodeToEmail(RecoverPassword recoverPassword) throws CompletionException {
        try {
            String recoverCode = generateVerificationCode();
            String expiration = ZonedDateTime.now().toString();

            RecoverPasswordDto recoverPasswordDto = new RecoverPasswordDto(recoverCode, recoverPassword.getNewUserPassword());
            recoverPasswordCaffeineCacheManager.setRecoverPasswordDto(recoverPassword.getUserEmail(), recoverPasswordDto);

            mailService.sendRecoverPasswordVerificationMail(
                    recoverPassword.getUserEmail().toString(),
                    recoverCode,
                    expiration
            );
        } catch (Exception e) {
            log.error("Error saving recover password or sending recover code: {}", e.getMessage(), e);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveUserPasswordInDatabaseAndClearRecoverPasswordCacheByUserEmail(UserPassword userPassword, UserEmail userEmail) {
        try {
            passwordRepository.saveUserPassword(userPassword);
            recoverPasswordCaffeineCacheManager.clearRecoverPasswordDto(userEmail);
        } catch (Exception e) {
            log.error("Error saving user password: {}", e.getMessage(), e);
        }
    }

    private CompletableFuture<Void> saveUserInCacheAndSendMessage(User user, UserPassword userPassword) throws CompletionException {
        try {
            String verificationCode = generateVerificationCode();

            userVerificationCaffeineCacheManager.setUser(user.getEmail(), user);
            userVerificationCaffeineCacheManager.setUserPassword(user.getEmail(), userPassword);
            userVerificationCaffeineCacheManager.setVerificationCode(user.getEmail(), verificationCode);

            String expiration = ZonedDateTime.now().toString();

            try {
                mailService.sendVerificationMail(user.getEmail().toString(), verificationCode, expiration);
            } catch (Exception ex) {
                log.error("Error sending verification mail: {}", ex.getMessage(), ex);
            }

            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("Error while save user in cache: {}", ex.getMessage(), ex);
            return CompletableFuture.completedFuture(null);
        }
    }

    private String generateVerificationCode() {
        StringBuilder verificationCode = new StringBuilder();
        Random random = new Random();

        int CODE_LENGTH = 4;
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomInt = random.nextInt(10);
            verificationCode.append(randomInt);
        }

        log.info("Generated verification code: {}", verificationCode);
        return verificationCode.toString();
    }
}

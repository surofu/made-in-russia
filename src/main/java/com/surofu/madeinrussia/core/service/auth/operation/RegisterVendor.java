package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class RegisterVendor {
    UserEmail userEmail;
    UserLogin userLogin;
    UserPasswordPassword userPasswordPassword;
    UserRegion userRegion;
    UserPhoneNumber userPhoneNumber;
    UserAvatar avatar;
    VendorDetailsInn vendorDetailsInn;
    List<VendorCountryName> vendorCountryNames;
    List<VendorProductCategoryName> vendorProductCategoryNames;
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserEmail userEmail) {
            log.info("Successfully processed register vendor: {}", userEmail.toString());
            return Success.of(userEmail);
        }

        static Result userWithEmailAlreadyExists(UserEmail userEmail) {
            log.warn("User with email '{}' already exists", userEmail);
            return UserWithEmailAlreadyExists.of(userEmail);
        }

        static Result userWithLoginAlreadyExists(UserLogin userLogin) {
            log.warn("User with login '{}' already exists", userLogin);
            return UserWithLoginAlreadyExists.of(userLogin);
        }

        static Result userWithPhoneNumberAlreadyExists(UserPhoneNumber userPhoneNumber) {
            log.warn("User with phone number '{}' already exists", userPhoneNumber);
            return UserWithPhoneNumberAlreadyExists.of(userPhoneNumber);
        }

        static Result vendorWithInnAlreadyExists(VendorDetailsInn inn) {
            log.warn("Vendor with inn '{}' already exists", inn.toString());
            return VendorWithInnAlreadyExists.of(inn);
        }

        static Result translationError(Exception e) {
            log.error("Translation error while register vendor", e);
            return TranslationError.INSTANCE;
        }

        static Result sendMailError(Exception e) {
            log.error("Send mail error while register vendor", e);
            return SendMailError.INSTANCE;
        }

        static Result saveInCacheError(Exception e) {
            log.error("Save cache error while register vendor", e);
            return SaveInCacheError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            UserEmail userEmail;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserWithEmailAlreadyExists implements Result {
            UserEmail userEmail;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithEmailAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserWithLoginAlreadyExists implements Result {
            UserLogin userLogin;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithLoginAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserWithPhoneNumberAlreadyExists implements Result {
            UserPhoneNumber userPhoneNumber;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithPhoneNumberAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class VendorWithInnAlreadyExists implements Result {
            VendorDetailsInn inn;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processVendorWithInnAlreadyExists(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum SendMailError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSendMailError(this);
            }
        }

        enum SaveInCacheError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveInCacheError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processUserWithEmailAlreadyExists(UserWithEmailAlreadyExists result);
            T processUserWithLoginAlreadyExists(UserWithLoginAlreadyExists result);
            T processUserWithPhoneNumberAlreadyExists(UserWithPhoneNumberAlreadyExists result);
            T processVendorWithInnAlreadyExists(VendorWithInnAlreadyExists result);
            T processTranslationError(TranslationError result);
            T processSendMailError(SendMailError result);
            T processSaveInCacheError(SaveInCacheError result);
        }
    }
}

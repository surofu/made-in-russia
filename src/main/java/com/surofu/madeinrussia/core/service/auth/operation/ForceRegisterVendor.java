package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.core.model.user.UserAvatar;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class ForceRegisterVendor {
    UserEmail email;
    UserLogin login;
    UserPasswordPassword password;
    UserPhoneNumber phoneNumber;
    VendorDetailsInn inn;
    List<VendorCountryName> countryNames;
    List<VendorProductCategoryName> productCategoryNames;
    UserAvatar avatar;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserEmail email) {
            log.info("Successfully processed force register vendor: {}", email);
            return Success.of(email);
        }

        static Result vendorWithEmailAlreadyExists(UserEmail email) {
            log.warn("Vendor with email '{}' already exists", email);
            return VendorWithEmailAlreadyExists.of(email);
        }

        static Result vendorWithLoginAlreadyExists(UserLogin login) {
            log.warn("Vendor with login '{}' already exists", login);
            return VendorWithLoginAlreadyExists.of(login);
        }

        static Result vendorWithPhoneNumberAlreadyExists(UserPhoneNumber phoneNumber) {
            log.warn("Vendor with phone number '{}' already exists", phoneNumber);
            return VendorWithPhoneNumberAlreadyExists.of(phoneNumber);
        }

        static Result vendorWithInnAlreadyExists(VendorDetailsInn inn) {
            log.warn("Vendor with inn '{}' already exists", inn);
            return VendorWithInnAlreadyExists.of(inn);
        }

        static Result emptyVendorCountries() {
            log.warn("Empty vendor countries while processing force register vendor");
            return EmptyVendorCountries.INSTANCE;
        }

        static Result emptyVendorProductCategories() {
            log.warn("Empty vendor products category while processing force register vendor");
            return EmptyVendorProductCategories.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.error("Translation error while force register vendor", e);
            return TranslationError.INSTANCE;
        }

        static Result saveError(Exception e) {
            log.error("Error while saving force register vendor", e);
            return SaveError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            UserEmail email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class VendorWithEmailAlreadyExists implements Result {
            UserEmail email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processVendorWithEmailAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class VendorWithLoginAlreadyExists implements Result {
            UserLogin login;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processVendorWithLoginAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class VendorWithPhoneNumberAlreadyExists implements Result {
            UserPhoneNumber phoneNumber;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processVendorWithPhoneNumberAlreadyExists(this);
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

        enum EmptyVendorCountries implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyVendorCountries(this);
            }
        }

        enum EmptyVendorProductCategories implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyVendorProductCategories(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processVendorWithEmailAlreadyExists(VendorWithEmailAlreadyExists result);
            T processVendorWithLoginAlreadyExists(VendorWithLoginAlreadyExists result);
            T processVendorWithPhoneNumberAlreadyExists(VendorWithPhoneNumberAlreadyExists result);
            T processVendorWithInnAlreadyExists(VendorWithInnAlreadyExists result);
            T processEmptyVendorCountries(EmptyVendorCountries result);
            T processEmptyVendorProductCategories(EmptyVendorProductCategories result);
            T processTranslationError(TranslationError result);
            T processSaveError(SaveError result);
        }
    }
}

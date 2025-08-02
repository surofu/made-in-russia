package com.surofu.madeinrussia.core.service.vendor.operation;

import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class ForceUpdateVendorById {
    Long id;
    UserEmail email;
    UserLogin login;
    UserPhoneNumber phoneNumber;
    VendorDetailsInn inn;
    List<VendorCountryName> vendorCountries;
    List<VendorProductCategoryName> vendorProductCategories;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long id) {
            log.info("Successfully force update vendor by ID '{}'", id);
            return Success.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("Vendor not found by ID '{}'", id);
            return NotFound.of(id);
        }

        static Result emailAlreadyExists(UserEmail email) {
            log.warn("Vendor with email '{}' already exists", email.toString());
            return EmailAlreadyExists.of(email);
        }

        static Result loginAlreadyExists(UserLogin login) {
            log.warn("Vendor with login '{}' already exists", login.toString());
            return LoginAlreadyExists.of(login);
        }

        static Result phoneNumberAlreadyExists(UserPhoneNumber phoneNumber) {
            log.warn("Vendor with phone number '{}' already exists", phoneNumber.toString());
            return PhoneNumberAlreadyExists.of(phoneNumber);
        }

        static Result innAlreadyExists(VendorDetailsInn inn) {
            log.warn("Vendor with inn '{}' already exists", inn.toString());
            return InnAlreadyExists.of(inn);
        }

        static Result emptyVendorCountries() {
            log.warn("Empty vendor countries while processing force update vendor by id");
            return EmptyVendorCountries.INSTANCE;
        }

        static Result emptyVendorProductCategories() {
            log.warn("Empty vendor products category while processing force update vendor");
            return EmptyVendorProductCategories.INSTANCE;
        }

        static Result translationError(Long id, Exception e) {
            log.error("Translation error while force update vendor by id: {}", id, e);
            return TranslationError.INSTANCE;
        }

        static Result saveError(Long id, Exception e) {
            log.error("Error while force update vendor by ID '{}'", id, e);
            return SaveError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long id;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        @Value(staticConstructor = "of")
        class EmailAlreadyExists implements Result {
            UserEmail email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmailAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class LoginAlreadyExists implements Result {
            UserLogin login;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processLoginAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class PhoneNumberAlreadyExists implements Result {
            UserPhoneNumber phoneNumber;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processPhoneNumberAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class InnAlreadyExists implements Result {
            VendorDetailsInn inn;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInnAlreadyExists(this);
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
            T processNotFound(NotFound result);
            T processEmailAlreadyExists(EmailAlreadyExists result);
            T processLoginAlreadyExists(LoginAlreadyExists result);
            T processInnAlreadyExists(InnAlreadyExists result);
            T processPhoneNumberAlreadyExists(PhoneNumberAlreadyExists result);
            T processEmptyVendorCountries(EmptyVendorCountries result);
            T processEmptyVendorProductCategories(EmptyVendorProductCategories result);
            T processTranslationError(TranslationError result);
            T processSaveError(SaveError result);
        }
    }
}

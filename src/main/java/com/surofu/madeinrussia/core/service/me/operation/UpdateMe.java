package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsAddress;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsDescription;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSiteUrl;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateMe {
    SecurityUser securityUser;
    UserPhoneNumber userPhoneNumber;
    UserRegion userRegion;
    VendorDetailsInn inn;
    VendorDetailsAddress address;
    VendorDetailsDescription description;
    List<VendorCountryName> countryNames;
    List<VendorProductCategoryName> categoryNames;
    List<VendorPhoneNumberPhoneNumber> phoneNumbers;
    List<VendorEmailEmail> emails;
    List<VendorSiteUrl> sites;
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto accountDto) {
            log.info("Successfully processed update me");
            return Success.of(accountDto);
        }

        static Result translationError(Exception e) {
            log.error("Translation error while update me", e);
            return TranslationError.INSTANCE;
        }

        static Result phoneNumberAlreadyExists(UserPhoneNumber phoneNumber) {
            log.warn("Phone number already in use: {}", phoneNumber);
            return PhoneNumberAlreadyExists.of(phoneNumber);
        }

        static Result saveError(Exception e) {
            log.error("Error saving update me", e);
            return SaveError.INSTANCE;
        }

        static Result innAlreadyExists(VendorDetailsInn inn) {
            log.warn("Inn already in use while update me: {}", inn);
            return InnAlreadyExists.of(inn);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AbstractAccountDto accountDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
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

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processTranslationError(TranslationError result);
            T processPhoneNumberAlreadyExists(PhoneNumberAlreadyExists result);
            T processInnAlreadyExists(InnAlreadyExists result);
            T processSaveError(SaveError result);
        }
    }
}

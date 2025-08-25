package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsDescription;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsSite;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategoryName;
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
    VendorDetailsDescription description;
    VendorDetailsSite site;
    List<VendorCountryName> countryNames;
    List<VendorProductCategoryName> categoryNames;
    List<VendorPhoneNumberPhoneNumber> phoneNumbers;
    List<VendorEmailEmail> emails;
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto accountDto) {
            log.info("Successfully processed update me: {}", accountDto);
            return Success.of(accountDto);
        }

        static Result translationError(Exception e) {
            log.error("Translation error while update me", e);
            return TranslationError.INSTANCE;
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

        interface Processor<T> {
            T processSuccess(Success result);
            T processTranslationError(TranslationError result);
        }
    }
}

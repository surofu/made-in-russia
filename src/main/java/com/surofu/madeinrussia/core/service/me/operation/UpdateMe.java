package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.vendorCountry.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorProductCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorProductCategory.VendorProductCategoryName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateMe {
    SecurityUser securityUser;
    UserPhoneNumber userPhoneNumber;
    UserRegion userRegion;
    VendorDetailsInn inn;
    List<VendorCountryName> countryNames;
    List<VendorProductCategoryName> categoryNames;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto accountDto) {
            log.info("Successfully processed update me: {}", accountDto);
            return Success.of(accountDto);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AbstractAccountDto accountDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
        }
    }
}

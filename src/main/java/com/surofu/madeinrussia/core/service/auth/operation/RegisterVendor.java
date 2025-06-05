package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.userPassword.UserPasswordPassword;
import com.surofu.madeinrussia.core.model.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorProductCategory.VendorProductCategoryName;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class RegisterVendor {
    UserEmail userEmail;
    UserLogin userLogin;
    UserPasswordPassword userPasswordPassword;
    UserRegion userRegion;
    UserPhoneNumber userPhoneNumber;

    VendorDetailsInn vendorDetailsInn;

    List<VendorCountryName> vendorCountryNames;
    List<VendorProductCategoryName> vendorProductCategoryNames;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(SimpleResponseMessageDto responseMessageDto) {
            log.info("Successfully processed register vendor with message: {}", responseMessageDto.getMessage());
            return Success.of(responseMessageDto);
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

        @Value(staticConstructor = "of")
        class Success implements Result {
            SimpleResponseMessageDto responseMessageDto;

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

        interface Processor<T> {
            T processSuccess(Success result);
            T processUserWithEmailAlreadyExists(UserWithEmailAlreadyExists result);
            T processUserWithLoginAlreadyExists(UserWithLoginAlreadyExists result);
            T processUserWithPhoneNumberAlreadyExists(UserWithPhoneNumberAlreadyExists result);
        }
    }
}

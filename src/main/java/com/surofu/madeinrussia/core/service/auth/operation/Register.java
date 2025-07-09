package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class Register {
    UserEmail userEmail;
    UserLogin userLogin;
    UserPasswordPassword userPasswordPassword;
    UserRegion userRegion;
    UserPhoneNumber userPhoneNumber;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserEmail userEmail) {
            log.info("Successfully processed register user: {}", userEmail.toString());
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

        interface Processor<T> {
            T processSuccess(Success result);
            T processUserWithEmailAlreadyExists(UserWithEmailAlreadyExists result);
            T processUserWithLoginAlreadyExists(UserWithLoginAlreadyExists result);
            T processUserWithPhoneNumberAlreadyExists(UserWithPhoneNumberAlreadyExists result);
        }
    }
}

package com.surofu.exporteru.core.service.auth.operation;

import com.surofu.exporteru.core.model.user.*;
import com.surofu.exporteru.core.model.user.password.UserPasswordPassword;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class ForceRegister {
    UserEmail email;
    UserLogin login;
    UserPasswordPassword password;
    UserRegion region;
    UserPhoneNumber phoneNumber;
    UserAvatar avatar;
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserEmail email) {
            log.info("Successfully processed force register user: {}", email);
            return Success.of(email);
        }

        static Result userWithEmailAlreadyExists(UserEmail email) {
            log.warn("User with email '{}' already exists", email);
            return UserWithEmailAlreadyExists.of(email);
        }

        static Result userWithLoginAlreadyExists(UserLogin login) {
            log.warn("User with login '{}' already exists", login);
            return UserWithLoginAlreadyExists.of(login);
        }

        static Result userWithPhoneNumberAlreadyExists(UserPhoneNumber phoneNumber) {
            log.warn("User with phone number '{}' already exists", phoneNumber);
            return UserWithPhoneNumberAlreadyExists.of(phoneNumber);
        }

        static Result saveError(Exception e) {
            log.error("Error while saving force register user", e);
            return SaveError.INSTANCE;
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
            UserEmail email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithEmailAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserWithLoginAlreadyExists implements Result {
            UserLogin login;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithLoginAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserWithPhoneNumberAlreadyExists implements Result {
            UserPhoneNumber phoneNumber;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithPhoneNumberAlreadyExists(this);
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
            T processUserWithEmailAlreadyExists(UserWithEmailAlreadyExists result);
            T processUserWithLoginAlreadyExists(UserWithLoginAlreadyExists result);
            T processUserWithPhoneNumberAlreadyExists(UserWithPhoneNumberAlreadyExists result);
            T processSaveError(SaveError result);
        }
    }
}

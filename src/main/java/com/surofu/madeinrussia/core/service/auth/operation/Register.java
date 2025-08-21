package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class Register {
    UserEmail userEmail;
    UserLogin userLogin;
    UserPasswordPassword userPasswordPassword;
    UserRegion userRegion;
    UserPhoneNumber userPhoneNumber;
    UserAvatar avatar;
    Locale locale;

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

        static Result saveInCacheError(Exception e) {
            log.error("Error while save user in cache: {}", e.getMessage(), e);
            return SaveInCacheError.INSTANCE;
        }

        static Result sendMailError(Exception e) {
            log.error("Error while send mail: {}", e.getMessage(), e);
            return SendMailError.INSTANCE;
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

        enum SaveInCacheError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveInCacheError(this);
            }
        }

        enum SendMailError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSendMailError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processUserWithEmailAlreadyExists(UserWithEmailAlreadyExists result);
            T processUserWithLoginAlreadyExists(UserWithLoginAlreadyExists result);
            T processUserWithPhoneNumberAlreadyExists(UserWithPhoneNumberAlreadyExists result);
            T processSaveInCacheError(SaveInCacheError result);
            T processSendMailError(SendMailError result);
        }
    }
}

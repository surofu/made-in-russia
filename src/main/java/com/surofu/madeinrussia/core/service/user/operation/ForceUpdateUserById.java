package com.surofu.madeinrussia.core.service.user.operation;

import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class ForceUpdateUserById {
    Long id;
    UserEmail email;
    UserLogin login;
    UserPhoneNumber phoneNumber;
    UserRegion region;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long id) {
            log.info("Successfully force update user by ID '{}'", id);
            return Success.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("User not found by ID '{}'", id);
            return NotFound.of(id);
        }

        static Result saveError(Long id, Exception e) {
            log.error("Error while force update user by ID '{}'", id, e);
            return SaveError.INSTANCE;
        }

        static Result emailAlreadyExists(UserEmail email) {
            log.warn("User with email '{}' already exists", email.toString());
            return EmailAlreadyExists.of(email);
        }

        static Result loginAlreadyExists(UserLogin login) {
            log.warn("User with login '{}' already exists", login.toString());
            return LoginAlreadyExists.of(login);
        }

        static Result phoneNumberAlreadyExists(UserPhoneNumber phoneNumber) {
            log.warn("User with phone number '{}' already exists", phoneNumber.toString());
            return PhoneNumberAlreadyExists.of(phoneNumber);
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
            T processSaveError(SaveError result);
            T processEmailAlreadyExists(EmailAlreadyExists result);
            T processLoginAlreadyExists(LoginAlreadyExists result);
            T processPhoneNumberAlreadyExists(PhoneNumberAlreadyExists result);
        }
    }
}

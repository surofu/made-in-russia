package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.dto.RecoverPasswordSuccessDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.userPassword.UserPasswordPassword;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class VerifyRecoverPassword {
    UserEmail userEmail;
    String recoverCode;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(RecoverPasswordSuccessDto recoverPasswordSuccessDto, UserEmail userEmail) {
            log.info("Successfully recover password of '{}'", userEmail);
            return Success.of(recoverPasswordSuccessDto);
        }

        static Result emailNotFound(UserEmail userEmail) {
            log.warn("Error while verify recover password. Cache data not found for email '{}'", userEmail);
            return EmailNotFound.of(userEmail);
        }

        static Result invalidRecoverCode(UserEmail userEmail, String recoverCode) {
            log.warn("Error while verify recover password. Invalid recover code '{}' for email '{}'", recoverCode, userEmail);
            return InvalidRecoverCode.INSTANCE;
        }

        static Result userNotFound(UserEmail userEmail) {
            log.warn("Error while verify recover password. User not found for email '{}'", userEmail);
            return UserNotFound.of(userEmail);
        }

        static Result authenticationFailed(UserEmail userEmail, UserPasswordPassword userPasswordPassword) {
            log.warn("""
            Error while verify recover password.
            Authentication failed:
            email: {}
            password: {}
            """, userEmail, userPasswordPassword);

            return AuthenticationFailed.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            RecoverPasswordSuccessDto recoverPasswordSuccessDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class EmailNotFound implements Result {
            UserEmail userEmail;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmilNotFound(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserNotFound implements Result {
            UserEmail userEmail;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserNotFound(this);
            }
        }

        enum InvalidRecoverCode implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidRecoverCode(this);
            }
        }

        enum AuthenticationFailed implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processAuthenticationFailed(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processEmilNotFound(EmailNotFound result);

            T processInvalidRecoverCode(InvalidRecoverCode result);

            T processUserNotFound(UserNotFound result);

            T processAuthenticationFailed(AuthenticationFailed result);
        }
    }
}

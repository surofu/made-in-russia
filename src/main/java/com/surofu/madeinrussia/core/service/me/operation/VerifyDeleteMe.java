package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class VerifyDeleteMe {
    SecurityUser securityUser;
    String code;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(UserEmail email) {
            log.info("Successfully delete account with email '{}'", email.toString());
            return Success.INSTANCE;
        }

        static Result sendMailError(Exception e) {
            log.error("Error while deleting me account", e);
            return DeleteError.INSTANCE;
        }

        static Result confirmationNotFound(UserEmail email) {
            log.warn("Confirmation not found for account with email '{}'", email.toString());
            return ConfirmationNotFound.INSTANCE;
        }

        static Result invalidConfirmationCode(UserEmail email) {
            log.warn("Invalid confirmation code for account with email '{}'", email.toString());
            return InvalidConfirmationCode.INSTANCE;
        }

        static Result deleteError(UserEmail email, Exception e) {
            log.error("Error while deleting me account '{}'", email.toString(), e);
            return DeleteError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum ConfirmationNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processConfirmationNotFound(this);
            }
        }

        enum InvalidConfirmationCode implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidConfirmationCode(this);
            }
        }

        enum DeleteError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeleteError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processDeleteError(DeleteError result);
            T processConfirmationNotFound(ConfirmationNotFound result);
            T processInvalidConfirmationCode(InvalidConfirmationCode result);
        }
    }
}

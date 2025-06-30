package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class RecoverPassword {
    UserEmail userEmail;
    UserPasswordPassword newUserPassword;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserEmail userEmail) {
            log.info("Successfully sent recover password mail to '{}'", userEmail);
            return Success.of(userEmail);
        }

        static Result userNotFound(UserEmail userEmail) {
            log.warn("Error while recover user password. User with email '{}' not found", userEmail);
            return UserNotFound.of(userEmail);
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
        class UserNotFound implements Result {
            UserEmail userEmail;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processUserNotFound(UserNotFound result);
        }
    }
}

package com.surofu.exporteru.core.service.user.operation;

import com.surofu.exporteru.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteUserByEmail {
    UserEmail email;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(UserEmail email) {
            log.info("Successfully deleted user with email '{}'", email);
            return Success.INSTANCE;
        }

        static Result notFound(UserEmail email) {
            log.warn("User not found with email '{}'", email);
            return NotFound.of(email);
        }

        static Result deleteError(UserEmail email, Exception e) {
            log.error("Error while deleting user with email '{}'", email, e);
            return DeleteError.INSTANCE;
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
            UserEmail email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
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
            T processNotFound(NotFound result);
            T processDeleteError(DeleteError result);
        }
    }
}

package com.surofu.exporteru.core.service.user.operation;

import com.surofu.exporteru.core.model.user.UserLogin;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteUserByLogin {
    UserLogin login;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(UserLogin login) {
            log.info("Successfully deleted user with login '{}'", login);
            return Success.INSTANCE;
        }

        static Result notFound(UserLogin login) {
            log.warn("User not found with login '{}'", login);
            return NotFound.of(login);
        }

        static Result deleteError(UserLogin login, Exception e) {
            log.error("Error while deleting user with login '{}'", login, e);
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
            UserLogin login;

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

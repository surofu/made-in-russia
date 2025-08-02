package com.surofu.madeinrussia.core.service.user.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteUserById {
    Long id;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long id) {
            log.info("Successfully deleted user with ID '{}'", id);
            return Success.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("User not found with ID '{}'", id);
            return NotFound.of(id);
        }

        static Result deleteError(Long id, Exception e) {
            log.error("Error while deleting user with ID '{}'", id, e);
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
            Long id;

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

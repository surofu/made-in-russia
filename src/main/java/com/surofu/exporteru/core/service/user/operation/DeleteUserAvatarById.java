package com.surofu.exporteru.core.service.user.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteUserAvatarById {
    Long id;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully saved user avatar");
            return Success.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("User with ID '{}' not found", id);
            return NotFound.of(id);
        }

        static Result deleteError(Exception e) {
            log.error("Error deleting user avatar", e);
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

package com.surofu.madeinrussia.core.service.user.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class UnbanUserById {
    Long id;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long id) {
            log.info("Successfully processed unban user ID: {}", id);
            return Success.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("User with ID '{}' not found", id);
            return NotFound.of(id);
        }

        static Result saveError(Long id, Exception e) {
            log.error("Error processing unban user by ID: {}", id, e);
            return SaveError.INSTANCE;
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
        }
    }
}

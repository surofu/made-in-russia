package com.surofu.madeinrussia.core.service.user.operation;

import com.surofu.madeinrussia.core.model.user.UserRole;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class ChangeUserRoleById {
    Long id;
    UserRole role;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long id) {
            log.info("Successfully changed user role by id: {}", id);
            return Success.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("User with ID '{}' not found", id);
            return NotFound.of(id);
        }

        static Result saveError(Long id, Exception e) {
            log.error("Error while changing user role by id: {}", id, e);
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

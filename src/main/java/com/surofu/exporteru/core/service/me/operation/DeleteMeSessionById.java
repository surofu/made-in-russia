package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteMeSessionById {
    SecurityUser securityUser;
    Long sessionId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Long sessionId) {
            log.info("Successfully processed delete me session with id '{}'", sessionId);
            return Success.of(sessionId);
        }

        static Result notFound(Long sessionId) {
            log.warn("Session with id '{}' not found", sessionId);
            return NotFound.of(sessionId);
        }

        static Result deleteError(Exception e) {
            log.error("Delete error while processing delete me session", e);
            return DeleteError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Long sessionId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long sessionId;

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

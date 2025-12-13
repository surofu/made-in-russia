package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.user.User;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteMeReviewById {
    Long id;
    SecurityUser securityUser;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long id, User user) {
            log.info("Successfully deleted review with ID \"{}\", by user with gmail \"{}\"", id, user.getEmail());
            return Success.INSTANCE;
        }

        static Result notFound(Long id, User user) {
            log.warn("Review with ID \"{}\" and user email \"{}\" was not found", id, user.getEmail());
            return NotFound.of(id);
        }

        static Result deleteError(Long id, User user, Exception e) {
            log.error("Error when deleting review with ID \"{}\", user email \"{}\"", id, user.getEmail(), e);
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

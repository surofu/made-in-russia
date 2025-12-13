package com.surofu.exporteru.core.service.moderation.operation;

import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class SetProductReviewApproveStatus {
    Long id;
    ApproveStatus approveStatus;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long id, ApproveStatus status) {
            log.info("Successfully set approve status '{}' for review: {}", status.name(), id);
            return Success.of(status);
        }

        static Result saveError(Long id, Exception e) {
            log.error("Error while approving review: {}", id, e);
            return SaveError.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("No approving review found for id '{}'", id);
            return NotFound.of(id);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            ApproveStatus status;

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

            T processSaveError(SaveError result);

            T processNotFound(NotFound result);
        }
    }
}

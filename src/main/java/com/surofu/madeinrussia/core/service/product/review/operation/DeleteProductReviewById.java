package com.surofu.madeinrussia.core.service.product.review.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteProductReviewById {
    Long id;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Long id) {
            log.info("Successfully deleted product review with ID '{}'", id);
            return Success.INSTANCE;
        }

        static Result notFound(Long id) {
            log.warn("Product review with ID '{}' not found", id);
            return NotFound.of(id);
        }

        static Result deleteError(Long id, Exception e) {
            log.error("Error while deleting product review with ID '{}'", id, e);
            return DeleteError.INSTANCE;
        }

        static Result deleteMediaError(Long id, Exception e) {
            log.error("Error while deleting product review with ID '{}'", id, e);
            return DeleteMediaError.INSTANCE;
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

        enum DeleteMediaError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeleteMediaError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processNotFound(NotFound result);
            T processDeleteError(DeleteError result);
            T processDeleteMediaError(DeleteMediaError result);
        }
    }
}

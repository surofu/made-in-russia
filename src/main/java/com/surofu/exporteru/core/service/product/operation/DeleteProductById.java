package com.surofu.exporteru.core.service.product.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteProductById {
    Long productId;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long productId) {
            log.info("Successfully processed delete product by id: {}", productId);
            return Success.INSTANCE;
        }

        static Result notFound(Long productId) {
            log.warn("Product with id '{}' not found", productId);
            return NotFound.of(productId);
        }

        static Result deleteError(Exception e) {
            log.error("Error while deleting product: {}", e.getMessage());
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
            Long productId;

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

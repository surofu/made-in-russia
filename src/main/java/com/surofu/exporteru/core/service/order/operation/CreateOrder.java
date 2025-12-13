package com.surofu.exporteru.core.service.order.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class CreateOrder {
    Long productId;
    String firstName;
    String email;
    String phoneNumber;
    Integer quantity;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Long productId, String email) {
            log.info("Successfully created a new order for product {} with email {}", productId, email);
            return Success.INSTANCE;
        }

        static Result notFound(Long productId) {
            log.warn("The product with id {} does not exist.", productId);
            return NotFound.of(productId);
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
                return processor.processProductNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processProductNotFound(NotFound result);
        }
    }
}

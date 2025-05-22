package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductDeliveryMethodsByProductId {
    Long productId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<DeliveryMethodDto> deliveryMethodDtos) {
            log.info("Successfully processed get delivery methods by product id with total elements: {}", deliveryMethodDtos.size());
            return Success.of(deliveryMethodDtos);
        }

        static Result notFound(Long productId) {
            log.warn("Product with ID '{}' not found", productId);
            return NotFound.of(productId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<DeliveryMethodDto> deliveryMethodDtos;

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

        interface Processor<T> {
            T processSuccess(Success result);
            T processNotFound(NotFound result);
        }
    }
}

package com.surofu.madeinrussia.core.service.deliveryMethod.operation;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetDeliveryMethodById {
    Long deliveryMethodId;

    public interface Result {
        <T> T process(Processor<T> processor);
        static Result success(DeliveryMethodDto deliveryMethodDto) {
            log.info("Successfully processed delivery method: {}", deliveryMethodDto.getId());
            return Success.of(deliveryMethodDto);
        }

        static Result notFound(Long deliveryMethodId) {
            log.warn("Delivery method with ID '{}' not found", deliveryMethodId);
            return NotFound.of(deliveryMethodId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            DeliveryMethodDto deliveryMethodDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long deliveryMethodId;

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
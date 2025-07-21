package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.product.ProductCharacteristicDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductCharacteristicsByProductId {
    Long productId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<ProductCharacteristicDto> characteristicDtos) {
            log.info("Successfully processed get product characteristics by productId with total elements: {}", characteristicDtos.size());
            return Success.of(characteristicDtos);
        }

        static Result notFound(Long productId) {
            log.info("Product with ID '{}' not found", productId);
            return NotFound.of(productId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<ProductCharacteristicDto> productCharacteristicDtos;

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

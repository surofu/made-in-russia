package com.surofu.exporteru.core.service.product.operation;

import com.surofu.exporteru.application.dto.product.ProductMediaDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductMediaByProductId {
    Long productId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<ProductMediaDto> productMediaDtos) {
            log.info("Successfully processed get product media by productId with total elements: {}", productMediaDtos.size());
            return Success.of(productMediaDtos);
        }

        static Result notFound(Long productId) {
            log.info("Product with ID '{}' not found", productId);
            return NotFound.of(productId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<ProductMediaDto> productMediaDtos;

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

package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.product.GetProductByIdQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductById {
    GetProductByIdQuery query;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(ProductDto productDto) {
            log.info("Successfully processed product: {}", productDto.getId());
            return Success.of(productDto);
        }

        static Result notFound(Long productId) {
            log.warn("Product with id '{}' not found", productId);
            return NotFound.of(productId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            ProductDto productDto;

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
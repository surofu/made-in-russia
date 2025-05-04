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
            log.info("Successfully retrieved product: {}", productDto);
            return Success.of(productDto);
        }

        static Result notFound() {
            return NotFound.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            ProductDto productDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum NotFound implements Result {
            INSTANCE;

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

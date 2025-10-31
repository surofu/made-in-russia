package com.surofu.exporteru.core.service.product.operation;

import com.surofu.exporteru.application.dto.category.CategoryDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductCategoryByProductId {
    Long productId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(CategoryDto categoryDto) {
            log.info("Successfully processed get product category by productId: {}", categoryDto.getId());
            return Success.of(categoryDto);
        }

        static Result notFound(Long productId) {
            log.warn("Product with ID '{}' not found", productId);
            return NotFound.of(productId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            CategoryDto categoryDto;

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

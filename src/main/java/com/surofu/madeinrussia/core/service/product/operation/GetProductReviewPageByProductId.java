package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductReviewDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductReviewPageByProductId {
    Long productId;
    int page;
    int size;
    Integer minRating;
    Integer maxRating;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Page<ProductReviewDto> productReviewDtoPage) {
            log.info("Successfully processed get product review dto page with total elements: {}", productReviewDtoPage.getTotalElements());
            return Success.of(productReviewDtoPage);
        }

        static Result notFound(Long productId) {
            log.warn("Product with ID '{}' not found", productId);
            return NotFound.of(productId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Page<ProductReviewDto> productReviewDtoPage;

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

package com.surofu.madeinrussia.core.service.productReview.operation;

import com.surofu.madeinrussia.application.dto.ProductReviewDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductReviewPageByProductId {
    Long productId;
    Integer page;
    Integer size;
    Integer minRating;
    Integer maxRating;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Page<ProductReviewDto> productReviewDtoPage) {
            log.info("Successfully processed get product review dto page with total elements: {}", productReviewDtoPage.getTotalElements());
            return Success.of(productReviewDtoPage);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Page<ProductReviewDto> productReviewDtoPage;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
        }
    }
}

package com.surofu.exporteru.core.service.product.review.operation;

import com.surofu.exporteru.application.dto.product.ProductReviewDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductReviewPageByProductId {
    Long productId;
    Integer page;
    Integer size;
    Integer minRating;
    Integer maxRating;
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Page<ProductReviewDto> page) {
            log.info("Successfully processed get product review page by product id: {}", page.getTotalElements());
            return Success.of(page);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Page<ProductReviewDto> page;

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

package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.dto.product.ProductReviewDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeReviewPage {
    SecurityUser securityUser;
    Integer page;
    Integer size;
    Integer minRating;
    Integer maxRating;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Page<ProductReviewDto> productReviewDtoPage) {
            log.info("Successfully processed get me product review page with total elements: {}", productReviewDtoPage.getTotalElements());
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

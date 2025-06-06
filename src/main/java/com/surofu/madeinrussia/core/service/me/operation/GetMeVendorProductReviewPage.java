package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.ProductReviewDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeVendorProductReviewPage {
    SecurityUser securityUser;
    Integer page;
    Integer size;
    Integer minRating;
    Integer maxRating;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Page<ProductReviewDto> vendorProductReviewDtoPage) {
            log.info("Successfully processed get me vendor product review page with total elements: {}", vendorProductReviewDtoPage.getTotalElements());
            return Success.of(vendorProductReviewDtoPage);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Page<ProductReviewDto> vendorProductReviewDtoPage;

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

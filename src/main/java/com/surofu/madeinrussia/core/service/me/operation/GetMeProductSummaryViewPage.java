package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeProductSummaryViewPage {
    Locale locale;
    SecurityUser securityUser;
    Integer page;
    Integer size;
    String title;
    List<Long> deliveryMethodIds;
    List<Long> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    List<ApproveStatus> approveStatuses;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Page<ProductSummaryViewDto> productSummaryViewDtoPage) {
            log.info("Successfully processed get me product summary view page with total elements: {}", productSummaryViewDtoPage.getTotalElements());
            return Success.of(productSummaryViewDtoPage);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {

            Page<ProductSummaryViewDto> productSummaryViewDtoPage;

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

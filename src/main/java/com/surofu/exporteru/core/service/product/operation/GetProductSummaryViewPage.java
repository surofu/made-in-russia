package com.surofu.exporteru.core.service.product.operation;

import com.surofu.exporteru.application.dto.product.ProductSummaryViewDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductSummaryViewPage {
    Locale locale;
    int page;
    int size;
    String title;
    List<Long> deliveryMethodIds;
    List<Long> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    List<ApproveStatus> approveStatuses;
    String sort;
    String direction;

    @Nullable
    SecurityUser securityUser;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Page<ProductSummaryViewDto> productSummaryViewDtoPage) {
            log.info("Successfully processed get product summary view dto page with total elements: {}", productSummaryViewDtoPage.getTotalElements());
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

package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductSummaryViewDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductSummaryViewPage {
    int page;
    int size;
    String title;
    List<Long> deliveryMethodIds;
    List<Long> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;

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

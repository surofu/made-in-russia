package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductSummaryViewDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductSummaryViewsByIds {
    Locale locale;
    List<Long> productIds;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(List<ProductSummaryViewDto> productSummaryViewDtos) {
            log.info("Successfully processed get product summary views by ids: {}", productSummaryViewDtos.size());
            return Success.of(productSummaryViewDtos);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<ProductSummaryViewDto> productSummaryViewDtos;

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

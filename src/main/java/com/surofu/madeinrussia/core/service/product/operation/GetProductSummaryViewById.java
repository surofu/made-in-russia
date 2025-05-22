package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductSummaryViewDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductSummaryViewById {
    Long productSummaryId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(ProductSummaryViewDto productSummaryViewDto) {
            log.info("Successfully processed get product summary view dto by id: {}", productSummaryViewDto);
            return Success.of(productSummaryViewDto);
        }

        static Result notFound(Long productSummaryId) {
            log.info("Product summary view with id '{}' not found", productSummaryId);
            return NotFound.of(productSummaryId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            ProductSummaryViewDto productSummaryViewDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long productSummaryId;

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

package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductPage {
    int page;
    int size;
    List<Long> deliveryMethodIds;
    List<Long> categoryIds;
    BigDecimal minPrice;
    BigDecimal maxPrice;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Page<ProductDto> productDtoPage) {
            log.info("Successfully processed get product dto page with total elements: {}", productDtoPage.getTotalElements());
            return Success.of(productDtoPage);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Page<ProductDto> productDtoPage;

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
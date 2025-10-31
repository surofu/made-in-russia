package com.surofu.exporteru.core.service.product.operation;

import com.surofu.exporteru.application.dto.product.ProductDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductPage {
    Integer page;
    Integer size;
    String title;
    List<Long> deliveryMethodIds;
    List<Long> categoryIds;

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
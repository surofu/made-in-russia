package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.product.GetProductsQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
@Value(staticConstructor = "of")
public class GetProducts {
    GetProductsQuery query;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Page<ProductDto> productDtoPage) {
            log.info("Successfully processed products");
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

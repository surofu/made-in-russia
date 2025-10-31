package com.surofu.exporteru.core.service.product.operation;

import com.surofu.exporteru.application.dto.product.ProductWithTranslationsDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetProductWithTranslationsById {
    Long id;
    Locale locale;
    SecurityUser securityUser;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(ProductWithTranslationsDto productWithTranslationsDto) {
            log.info("Successfully process get product with translations by ID: {}", productWithTranslationsDto.getId());
            return Success.of(productWithTranslationsDto);
        }

        static Result notFound(Long id) {
            log.warn("Product with translations with ID '{}' not found", id);
            return NotFound.of(id);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            ProductWithTranslationsDto productWithTranslationsDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long id;

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

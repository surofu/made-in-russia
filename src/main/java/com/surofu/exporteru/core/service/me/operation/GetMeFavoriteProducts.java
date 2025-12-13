package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.dto.product.ProductSummaryViewDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeFavoriteProducts {
    SecurityUser securityUser;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(List<ProductSummaryViewDto> dtos) {
            log.info("Successfully processed {} favorite products", dtos.size());
            return Success.of(dtos);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<ProductSummaryViewDto> products;

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

package com.surofu.madeinrussia.core.service.general.operation;

import com.surofu.madeinrussia.application.dto.GeneralDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetAllGeneral {
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(GeneralDto dto) {
            log.info("Successfully processed get all general with: products({}), categories({})", dto.getProducts().getTotalElements(), dto.getCategories().size());
            return Success.of(dto);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            GeneralDto dto;

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

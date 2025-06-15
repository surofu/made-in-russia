package com.surofu.madeinrussia.core.service.category.operation;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetAllCategories {

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<CategoryDto> categories) {
            log.info("Successfully processed {} categories", categories.size());
            return Success.of(categories);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<CategoryDto> categoryDtos;

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

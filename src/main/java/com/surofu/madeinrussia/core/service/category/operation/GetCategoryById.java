package com.surofu.madeinrussia.core.service.category.operation;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetCategoryById {
    Long categoryId;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(CategoryDto categoryDto) {
            log.info("Successfully processed get category by id: {}", categoryDto.getId());
            return Success.of(categoryDto);
        }

        static Result notFound(Long categoryId) {
            log.warn("Category with ID '{}' not found", categoryId);
            return NotFound.of(categoryId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            CategoryDto categoryDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long categoryId;

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
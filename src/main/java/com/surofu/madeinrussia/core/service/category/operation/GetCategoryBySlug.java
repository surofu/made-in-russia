package com.surofu.madeinrussia.core.service.category.operation;

import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetCategoryBySlug {
    CategorySlug categorySlug;
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(CategoryDto categoryDto) {
            log.info("Successfully processed get category by slug: {}", categoryDto.getSlug());
            return Success.of(categoryDto);
        }

        static Result notFound(CategorySlug categorySlug) {
            log.warn("Category with slug '{}' not found", categorySlug.toString());
            return NotFound.of(categorySlug);
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
            CategorySlug categorySlug;

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
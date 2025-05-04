package com.surofu.madeinrussia.core.service.category.operation;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.application.query.category.GetCategoryByIdQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetCategoryById {
    GetCategoryByIdQuery query;

    public interface  Result {
        <T> T process(Processor<T> processor);

        static Result success(CategoryDto categoryDto) {
            return Success.of(categoryDto);
        }

        static Result notFound() {
            return NotFound.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            CategoryDto categoryDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum NotFound implements Result {
            INSTANCE;

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

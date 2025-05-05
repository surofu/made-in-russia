package com.surofu.madeinrussia.core.service.category.operation;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Operation class for retrieving product categories.
 * Encapsulates the category retrieval process and handles the result processing.
 */
@Slf4j
@Value(staticConstructor = "of")
public class GetCategories {

    /**
     * Interface representing the result of the categories retrieval operation.
     * Uses the Visitor pattern to allow flexible processing of results.
     */
    public interface Result {
        /**
         * Processes the result using the provided processor
         * @param processor The processor implementation to handle the result
         * @param <T> The return type of the processor
         * @return The processed result of type T
         */
        <T> T process(Processor<T> processor);

        /**
         * Creates a success result with the retrieved categories
         * @param categories List of category DTOs
         * @return Success result instance
         * @throws IllegalArgumentException if categories list is null
         */
        static Result success(List<CategoryDto> categories) {
            log.info("Successfully processed {} categories", categories.size());
            return Success.of(categories);
        }

        /**
         * Success implementation containing the retrieved categories.
         */
        @Value(staticConstructor = "of")
        class Success implements Result {
            /**
             * The list of successfully retrieved category DTOs
             */
            List<CategoryDto> categories;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        /**
         * Processor interface for handling successful results
         * @param <T> The type of the processing result
         */
        interface Processor<T> {
            /**
             * Processes a successful categories retrieval result
             * @param result The success result containing categories
             * @return The processing result
             */
            T processSuccess(Success result);
        }
    }
}

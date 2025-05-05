package com.surofu.madeinrussia.core.service.category.operation;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.application.query.category.GetCategoryByIdQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Operation class for retrieving a category by its ID.
 * Encapsulates the query parameters and handles both success and not-found scenarios.
 */
@Slf4j
@Value(staticConstructor = "of")
public class GetCategoryById {

    /**
     * The query containing the category ID to retrieve
     */
    GetCategoryByIdQuery query;

    /**
     * Interface representing the result of the category retrieval operation.
     * Uses the Visitor pattern to allow different processing of success and not-found cases.
     */
    public interface Result {
        /**
         * Processes the result using the provided processor
         * @param processor The processor to handle the result
         * @param <T> The type of the processing result
         * @return The processed result
         * @throws IllegalArgumentException if processor is null
         */
        <T> T process(Processor<T> processor);

        /**
         * Creates a success result with the retrieved category
         * @param categoryDto The successfully retrieved category DTO
         * @return Success result instance
         * @throws IllegalArgumentException if categoryDto is null
         */
        static Result success(CategoryDto categoryDto) {
            log.info("Successfully retrieved category with ID: {}", categoryDto.getId());
            return Success.of(categoryDto);
        }

        /**
         * Creates a not-found result when the category doesn't exist
         * @param categoryId The ID of the category that wasn't found
         * @return Not-found result instance
         * @throws IllegalArgumentException if categoryId is null
         */
        static Result notFound(Long categoryId) {
            log.warn("Category with ID '{}' not found", categoryId);
            return NotFound.of(categoryId);
        }

        /**
         * Success implementation of the Result interface.
         * Contains the successfully retrieved category.
         */
        @Value(staticConstructor = "of")
        class Success implements Result {
            /**
             * The successfully retrieved category DTO
             */
            CategoryDto categoryDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        /**
         * Not-found implementation of the Result interface.
         * Contains the ID of the category that wasn't found.
         */
        @Value(staticConstructor = "of")
        class NotFound implements Result {
            /**
             * The ID of the category that wasn't found
             */
            Long categoryId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        /**
         * Processor interface for handling different result types
         * @param <T> The type of the processing result
         */
        interface Processor<T> {
            /**
             * Processes a successful result
             * @param result The success result containing the category
             * @return The processing result
             */
            T processSuccess(Success result);

            /**
             * Processes a not-found result
             * @param result The not-found result containing the category ID
             * @return The processing result
             */
            T processNotFound(NotFound result);
        }
    }
}
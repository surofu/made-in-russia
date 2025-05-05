package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.product.GetProductByIdQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Operation class for retrieving a single product by its ID.
 * Encapsulates the query parameters and handles both success and not-found cases.
 */
@Slf4j
@Value(staticConstructor = "of")
public class GetProductById {

    /**
     * The query containing the product ID to retrieve
     */
    GetProductByIdQuery query;

    /**
     * Interface representing the result of the product retrieval operation.
     * Uses the Visitor pattern to allow different processing of success and not-found cases.
     */
    public interface Result {
        /**
         * Processes the result using the provided processor
         * @param processor The processor to handle the result
         * @param <T> The type of the processing result
         * @return The processed result
         */
        <T> T process(Processor<T> processor);

        /**
         * Creates a success result with the retrieved product
         * @param productDto The successfully retrieved product DTO
         * @return Success result instance
         */
        static Result success(ProductDto productDto) {
            log.info("Successfully processed product: {}", productDto.getId());
            return Success.of(productDto);
        }

        /**
         * Creates a not-found result when the product doesn't exist
         * @param productId The ID of the product that wasn't found
         * @return Not-found result instance
         */
        static Result notFound(Long productId) {
            log.warn("Product with id '{}' not found", productId);
            return NotFound.of(productId);
        }

        /**
         * Success implementation of the Result interface.
         * Contains the successfully retrieved product.
         */
        @Value(staticConstructor = "of")
        class Success implements Result {
            /**
             * The successfully retrieved product DTO
             */
            ProductDto productDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        /**
         * Not-found implementation of the Result interface.
         * Contains the ID of the product that wasn't found.
         */
        @Value(staticConstructor = "of")
        class NotFound implements Result {
            /**
             * The ID of the product that wasn't found
             */
            Long productId;

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
             * @param result The success result containing the product
             * @return The processing result
             */
            T processSuccess(Success result);

            /**
             * Processes a not-found result
             * @param result The not-found result containing the product ID
             * @return The processing result
             */
            T processNotFound(NotFound result);
        }
    }
}
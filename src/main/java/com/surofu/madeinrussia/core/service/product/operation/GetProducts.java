package com.surofu.madeinrussia.core.service.product.operation;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.product.GetProductsQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

/**
 * Operation class for retrieving paginated and filtered products.
 * Encapsulates the query parameters and handles the processing of results.
 */
@Slf4j
@Value(staticConstructor = "of")
public class GetProducts {

    /**
     * The query containing filtering and pagination parameters
     */
    GetProductsQuery query;

    /**
     * Interface representing the result of the products retrieval operation.
     * Uses the Visitor pattern to allow different processing of results.
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
         * Creates a success result with the retrieved products
         * @param productDtoPage Page of product DTOs
         * @return Success result instance
         */
        static Result success(Page<ProductDto> productDtoPage) {
            log.info("Successfully processed {} products", productDtoPage.getTotalElements());
            return Success.of(productDtoPage);
        }

        /**
         * Success implementation of the Result interface.
         * Contains the successfully retrieved page of products.
         */
        @Value(staticConstructor = "of")
        class Success implements Result {
            /**
             * The page of products retrieved
             */
            Page<ProductDto> productDtoPage;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        /**
         * Processor interface for handling different result types
         * @param <T> The type of the processing result
         */
        interface Processor<T> {
            /**
             * Processes a successful result
             * @param result The success result containing products
             * @return The processing result
             */
            T processSuccess(Success result);
        }
    }
}
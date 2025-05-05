package com.surofu.madeinrussia.core.service.deliveryMethod.operation;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.application.query.deliveryMethod.GetDeliveryMethodByIdQuery;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Operation class for retrieving a delivery method by its ID.
 * Encapsulates the query parameters and handles both success and not-found scenarios.
 */
@Slf4j
@Value(staticConstructor = "of")
public class GetDeliveryMethodById {

    /**
     * The query containing the delivery method ID to retrieve
     */
    @NotNull
    GetDeliveryMethodByIdQuery query;

    /**
     * Interface representing the result of the delivery method retrieval operation.
     * Uses the Visitor pattern to allow different processing of success and not-found cases.
     */
    public interface Result {
        /**
         * Processes the result using the provided processor
         * @param processor The processor to handle the result (must not be null)
         * @param <T> The type of the processing result
         * @return The processed result
         * @throws IllegalArgumentException if processor is null
         */
        <T> T process(Processor<T> processor);

        /**
         * Creates a success result with the retrieved delivery method
         * @param deliveryMethodDto The successfully retrieved delivery method DTO (must not be null)
         * @return Success result instance
         * @throws IllegalArgumentException if deliveryMethodDto is null
         */
        static Result success(@NotNull DeliveryMethodDto deliveryMethodDto) {
            log.info("Successfully retrieved delivery method with ID: {}", deliveryMethodDto.getId());
            return Success.of(deliveryMethodDto);
        }

        /**
         * Creates a not-found result when the delivery method doesn't exist
         * @param deliveryMethodId The ID of the delivery method that wasn't found (must not be null)
         * @return Not-found result instance
         * @throws IllegalArgumentException if deliveryMethodId is null
         */
        static Result notFound(@NotNull Long deliveryMethodId) {
            log.warn("Delivery method with ID '{}' not found", deliveryMethodId);
            return NotFound.of(deliveryMethodId);
        }

        /**
         * Success implementation of the Result interface.
         * Contains the successfully retrieved delivery method.
         */
        @Value(staticConstructor = "of")
        class Success implements Result {
            /**
             * The successfully retrieved delivery method DTO
             */
            @NotNull
            DeliveryMethodDto deliveryMethodDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        /**
         * Not-found implementation of the Result interface.
         * Contains the ID of the delivery method that wasn't found.
         */
        @Value(staticConstructor = "of")
        class NotFound implements Result {
            /**
             * The ID of the delivery method that wasn't found
             */
            @NotNull
            Long deliveryMethodId;

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
             * @param result The success result containing the delivery method
             * @return The processing result
             */
            T processSuccess(Success result);

            /**
             * Processes a not-found result
             * @param result The not-found result containing the delivery method ID
             * @return The processing result
             */
            T processNotFound(NotFound result);
        }
    }
}
package com.surofu.madeinrussia.core.service.deliveryMethod.operation;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Collections;

/**
 * Operation class for retrieving delivery methods.
 * Encapsulates the delivery method retrieval process and handles the result processing.
 */
@Slf4j
@Value(staticConstructor = "of")
public class GetDeliveryMethods {

    /**
     * Interface representing the result of the delivery methods retrieval operation.
     * Uses the Visitor pattern to allow flexible processing of results.
     */
    public interface Result {
        /**
         * Processes the result using the provided processor
         * @param processor The processor implementation to handle the result (must not be null)
         * @param <T> The return type of the processor
         * @return The processed result of type T
         * @throws IllegalArgumentException if processor is null
         */
        <T> T process(Processor<T> processor);

        /**
         * Creates a success result with the retrieved delivery methods
         * @param deliveryMethodDtoList List of delivery method DTOs (must not be null)
         * @return Success result instance
         * @throws IllegalArgumentException if deliveryMethodDtoList is null
         */
        static Result success(List<DeliveryMethodDto> deliveryMethodDtoList) {
            log.info("Successfully processed {} delivery methods", deliveryMethodDtoList.size());
            return Success.of(Collections.unmodifiableList(deliveryMethodDtoList));
        }

        /**
         * Success implementation containing the retrieved delivery methods.
         */
        @Value(staticConstructor = "of")
        class Success implements Result {
            /**
             * The list of successfully retrieved delivery method DTOs
             */
            List<DeliveryMethodDto> deliveryMethodDtos;

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
             * Processes a successful delivery methods retrieval result
             * @param result The success result containing delivery methods
             * @return The processing result
             */
            T processSuccess(Success result);
        }
    }
}
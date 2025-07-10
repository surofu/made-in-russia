package com.surofu.madeinrussia.core.service.deliveryMethod.operation;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Collections;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetDeliveryMethods {
    Locale locale;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<DeliveryMethodDto> deliveryMethodDtoList) {
            log.info("Successfully processed {} delivery methods", deliveryMethodDtoList.size());
            return Success.of(Collections.unmodifiableList(deliveryMethodDtoList));
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<DeliveryMethodDto> deliveryMethodDtos;

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
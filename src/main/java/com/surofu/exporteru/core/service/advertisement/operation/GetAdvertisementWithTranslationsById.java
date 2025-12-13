package com.surofu.exporteru.core.service.advertisement.operation;

import com.surofu.exporteru.application.dto.advertisement.AdvertisementWithTranslationsDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetAdvertisementWithTranslationsById {
    Long id;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(AdvertisementWithTranslationsDto advertisementDto) {
            log.info("Successfully processed get advertisement with translations by id: {}", advertisementDto.getId());
            return Success.of(advertisementDto);
        }

        static Result notFound(Long advertisementId) {
            log.warn("Advertisement with ID '{}' not found", advertisementId);
            return NotFound.of(advertisementId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AdvertisementWithTranslationsDto advertisementDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long advertisementId;

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

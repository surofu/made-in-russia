package com.surofu.exporteru.core.service.advertisement.operation;

import com.surofu.exporteru.application.dto.advertisement.AdvertisementWithTranslationsDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetAllAdvertisementsWithTranslations {
    Locale locale;
    String text;
    String sort;
    String direction;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(List<AdvertisementWithTranslationsDto> advertisementDtoList) {
            log.info("Successfully processed get all advertisements: {}", advertisementDtoList.size());
            return Success.of(advertisementDtoList);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<AdvertisementWithTranslationsDto> advertisementDtoList;

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

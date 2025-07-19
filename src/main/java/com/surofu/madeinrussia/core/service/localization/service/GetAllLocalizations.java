package com.surofu.madeinrussia.core.service.localization.service;

import com.surofu.madeinrussia.application.dto.WebLocalizationDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetAllLocalizations {

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(WebLocalizationDto dto) {
            log.info("Successfully processed get all localizations");
            return Success.of(dto);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            WebLocalizationDto webLocalizationDto;

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

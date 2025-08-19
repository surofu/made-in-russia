package com.surofu.madeinrussia.core.service.localization.service;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Value(staticConstructor = "of")
public class SaveLocalizationByLanguageCode {
    String languageCode;
    Map<String, Object> content;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed save localization by language code");
            return Success.INSTANCE;
        }

        static Result saveError(Exception e) {
            log.error("Error saving localization by language code", e);
            return SaveError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processSaveError(SaveError result);
        }
    }
}

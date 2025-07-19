package com.surofu.madeinrussia.core.service.localization.service;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Value(staticConstructor = "of")
public class GetLocalizationByLanguageCode {
    String languageCode;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Map<String, Object> content) {
            log.info("Successfully processed get localization by language code");
            return Success.of(content);
        }

        static Result notFound(String languageCode) {
            log.warn("Localization by language code '{}' not found", languageCode);
            return NotFound.of(languageCode);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Map<String, Object> content;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            String languageCode;

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

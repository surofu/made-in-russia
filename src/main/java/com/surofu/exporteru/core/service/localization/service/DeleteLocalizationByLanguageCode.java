package com.surofu.exporteru.core.service.localization.service;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteLocalizationByLanguageCode {
    String languageCode;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed delete localization by language code");
            return Success.INSTANCE;
        }

        static Result notFound(String languageCode) {
            log.warn("Localization by language code '{}' not found", languageCode);
            return NotFound.of(languageCode);
        }

        static Result deleteError(Exception e) {
            log.error("Error saving localization by language code", e);
            return DeleteError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

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

        enum DeleteError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processDeleteError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processNotFound(NotFound result);
            T processDeleteError(DeleteError result);
        }
    }
}

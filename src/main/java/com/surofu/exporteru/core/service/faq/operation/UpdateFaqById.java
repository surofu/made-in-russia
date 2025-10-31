package com.surofu.exporteru.core.service.faq.operation;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.core.model.faq.FaqAnswer;
import com.surofu.exporteru.core.model.faq.FaqQuestion;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateFaqById {
    Long faqId;
    FaqQuestion question;
    HstoreTranslationDto questionTranslations;
    FaqAnswer answer;
    HstoreTranslationDto answerTranslations;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long faqId) {
            log.info("Successfully processed update faq by id: {}", faqId);
            return Success.of(faqId);
        }

        static Result notFound(Long faqId) {
            log.warn("Faq with ID '{}' not found", faqId);
            return NotFound.of(faqId);
        }

        static Result emptyTranslations(Exception e) {
            log.warn("Empty translations: {}", e.getMessage());
            return EmptyTranslations.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.error("Translation error: {}", e.getMessage());
            return TranslationError.INSTANCE;
        }

        static Result saveFaqError(Exception e) {
            log.error("Faq error: {}", e.getMessage());
            return SaveFaqError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            Long faqId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long faqId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        enum EmptyTranslations implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyTranslations(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum SaveFaqError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveFaqError(this);
            }
        }


        interface Processor<T> {
            T processSuccess(Success result);

            T processNotFound(NotFound result);

            T processEmptyTranslations(EmptyTranslations result);

            T processTranslationError(TranslationError error);

            T processSaveFaqError(SaveFaqError error);
        }
    }
}

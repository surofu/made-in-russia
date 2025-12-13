package com.surofu.exporteru.core.service.faq.operation;

import com.surofu.exporteru.application.dto.faq.FaqDto;
import com.surofu.exporteru.core.model.faq.FaqAnswer;
import com.surofu.exporteru.core.model.faq.FaqQuestion;
import java.util.Map;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class CreateFaq {
    FaqQuestion question;
    Map<String, String> questionTranslations;
    FaqAnswer answer;
    Map<String, String> answerTranslations;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(FaqDto faq) {
            log.info("Successfully processed create faq");
            return Success.of(faq);
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
            log.error("Saving faq error: {}", e.getMessage());
            return SaveFaqError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            FaqDto faq;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
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
            T processEmptyTranslations(EmptyTranslations result);
            T processTranslationError(TranslationError result);
            T processSaveFaqError(SaveFaqError result);
        }
    }
}

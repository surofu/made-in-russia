package com.surofu.madeinrussia.core.service.faq.operation;

import com.surofu.madeinrussia.core.model.faq.FaqAnswer;
import com.surofu.madeinrussia.core.model.faq.FaqQuestion;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateFaqById {
    Long faqId;
    FaqQuestion question;
    FaqAnswer answer;

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

        interface Processor<T> {
            T processSuccess(Success result);

            T processNotFound(NotFound result);
        }
    }
}

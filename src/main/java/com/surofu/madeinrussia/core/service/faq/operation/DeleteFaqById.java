package com.surofu.madeinrussia.core.service.faq.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteFaqById {
    Long faqId;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long faqId) {
            log.info("Successfully processed delete faq by id: {}", faqId);
            return Success.INSTANCE;
        }

        static Result notFound(Long faqId) {
            log.warn("Faq with ID '{}' not found", faqId);
            return NotFound.of(faqId);
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

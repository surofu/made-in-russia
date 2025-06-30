package com.surofu.madeinrussia.core.service.faq.operation;

import com.surofu.madeinrussia.core.model.faq.FaqAnswer;
import com.surofu.madeinrussia.core.model.faq.FaqQuestion;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class CreateFaq {
    FaqQuestion question;
    FaqAnswer answer;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed create faq");
            return Success.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

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

package com.surofu.madeinrussia.core.service.faq.operation;

import com.surofu.madeinrussia.application.dto.faq.FaqDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetAllFaq {
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(List<FaqDto> faqDtos) {
            log.info("Successfully processed get all faq: {}", faqDtos.size());
            return Success.of(faqDtos);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<FaqDto> faqDtos;

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

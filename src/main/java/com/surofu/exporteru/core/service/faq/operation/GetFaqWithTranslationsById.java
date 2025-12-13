package com.surofu.exporteru.core.service.faq.operation;

import com.surofu.exporteru.application.dto.faq.FaqWithTranslationsDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetFaqWithTranslationsById {
    Long faqId;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(FaqWithTranslationsDto dto) {
            log.info("Successfully processed get faq with translations by id: {}", dto.getId());
            return Success.of(dto);
        }

        static Result notFound(Long faqId) {
            log.warn("Faq with ID '{}' not found", faqId);
            return NotFound.of(faqId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            FaqWithTranslationsDto dto;

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

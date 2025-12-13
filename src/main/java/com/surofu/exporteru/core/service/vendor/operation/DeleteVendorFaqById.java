package com.surofu.exporteru.core.service.vendor.operation;

import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteVendorFaqById {
    SecurityUser securityUser;
    Long faqId;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(Long faqId) {
            log.info("Successfully processed delete vendor faq by id: {}", faqId);
            return Success.of(faqId);
        }

        static Result notFound(Long faqId) {
            log.info("Not found delete vendor faq by id: {}", faqId);
            return NotFound.of(faqId);
        }

        static Result deleteError(Exception e) {
            log.error("Error while delete vendor faq: ", e);
            return DeleteError.INSTANCE;
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

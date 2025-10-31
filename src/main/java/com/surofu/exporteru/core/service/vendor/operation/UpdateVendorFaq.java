package com.surofu.exporteru.core.service.vendor.operation;

import com.surofu.exporteru.application.dto.vendor.VendorFaqDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaqAnswer;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaqQuestion;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateVendorFaq {
    Long id;
    VendorFaqQuestion question;
    VendorFaqAnswer answer;
    SecurityUser securityUser;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(VendorFaqDto faq) {
            log.info("Successfully processed vendor update faq");
            return Success.of(faq);
        }

        static Result notFound(Long id) {
            log.warn("Vendor faq with ID \"{}\" not found", id);
            return NotFound.of(id);
        }

        static Result saveError(Long id, Exception e) {
            log.error("Error processing update vendor faq b ID \"{}\"", id, e);
            return SaveError.INSTANCE;
        }

        static Result translationError(Long id, Exception e) {
            log.error("Error processing update vendor faq b ID \"{}\"", id, e);
            return TranslationError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            VendorFaqDto faq;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            Long id;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processNotFound(NotFound result);

            T processSaveError(SaveError result);

            T processTranslationError(TranslationError result);
        }
    }
}

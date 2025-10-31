package com.surofu.exporteru.core.service.vendor.operation;

import com.surofu.exporteru.application.dto.vendor.VendorFaqDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaqAnswer;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaqQuestion;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class CreateVendorFaq {
    SecurityUser securityUser;
    VendorFaqQuestion question;
    VendorFaqAnswer answer;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(VendorFaqDto faq) {
            log.info("Successfully processed vendor create faq");
            return Success.of(faq);
        }

        static Result translationError(Exception e) {
            log.error("Error processing vendor create faq", e);
            return TranslationError.INSTANCE;
        }

        static Result saveError(Exception e) {
            log.error("Error processing vendor create faq", e);
            return SaveError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            VendorFaqDto faq;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.process(this);
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
            T process(Success result);

            T processTranslationError(TranslationError result);

            T processSaveError(SaveError result);
        }
    }
}

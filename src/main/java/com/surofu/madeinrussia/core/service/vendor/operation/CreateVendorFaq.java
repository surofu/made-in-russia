package com.surofu.madeinrussia.core.service.vendor.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.vendorDetails.faq.VendorFaqAnswer;
import com.surofu.madeinrussia.core.model.vendorDetails.faq.VendorFaqQuestion;
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

        static Result success() {
            log.info("Successfully processed vendor create faq");
            return Success.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.process(this);
            }
        }

        interface Processor<T> {
            T process(Success result);
        }
    }
}

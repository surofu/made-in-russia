package com.surofu.exporteru.core.service.vendor.operation;

import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class SendCallRequestMail {
    Long vendorId;
    SecurityUser securityUser;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed send email request");
            return Success.INSTANCE;
        }

        static Result notFound(Long vendorId) {
            log.warn("Vendor with id '{}' not found", vendorId);
            return NotFound.of(vendorId);
        }

        static Result sendMailError(Exception e) {
            log.error("Error sending email request", e);
            return SendMailError.INSTANCE;
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
            Long vendorId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        enum SendMailError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSendMailError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processSendMailError(SendMailError result);

            T processNotFound(NotFound result);
        }
    }
}

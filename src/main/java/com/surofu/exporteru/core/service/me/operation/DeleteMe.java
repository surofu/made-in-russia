package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteMe {
    SecurityUser securityUser;
    Locale locale;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(UserEmail email) {
            log.info("Successfully sand confirmation mail to {}", email.toString());
            return Success.of(email);
        }

        static Result sendMailError(Exception e) {
            log.error("Error while sending delete confirmation account mail", e);
            return SendMailError.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            UserEmail email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
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
        }
    }
}

package com.surofu.exporteru.core.service.support.operation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class SendSupportMail {
    String username;
    String email;
    String subject;
    String body;
    List<MultipartFile> media;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(String email) {
            log.info("Successfully processed send support mail from: {}", email);
            return Success.INSTANCE;
        }

        static Result sendEmailError(Exception e) {
            log.error("Error processing send support mail:", e);
            return SendEmailError.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.error("Error processing send support mail:", e);
            return SendEmailError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum SendEmailError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSendEmailError(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processSendEmailError(SendEmailError result);

            T processTranslationError(TranslationError result);
        }
    }
}

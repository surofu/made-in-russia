package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Value(staticConstructor = "of")
public class SaveMeAvatar {
    MultipartFile file;
    SecurityUser securityUser;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully saved user avatar");
            return Success.INSTANCE;
        }

        static Result saveError(Exception e) {
            log.error("Error saving user avatar", e);
            return SaveError.INSTANCE;
        }

        static Result emptyFile() {
            log.warn("Empty file while save user avatar");
            return SaveError.INSTANCE;
        }

        static Result invalidContentType(String contentType) {
            log.warn("Invalid content type while save user avatar");
            return SaveError.valueOf(contentType);
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        enum EmptyFile implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyFile(this);
            }
        }

        @Value(staticConstructor = "of")
        class InvalidContentType implements Result {
            String contentType;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidContentType(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processSaveError(SaveError result);
            T processEmptyFile(EmptyFile result);
            T processInvalidContentType(InvalidContentType result);
        }
    }
}

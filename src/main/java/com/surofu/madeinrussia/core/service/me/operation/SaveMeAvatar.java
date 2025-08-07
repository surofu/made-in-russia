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

        interface Processor<T> {
            T processSuccess(Success result);
            T processSaveError(SaveError result);
        }
    }
}

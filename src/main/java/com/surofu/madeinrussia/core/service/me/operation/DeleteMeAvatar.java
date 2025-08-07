package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class DeleteMeAvatar {
    SecurityUser securityUser;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully deleted user avatar");
            return Success.INSTANCE;
        }

        static Result deleteError(Exception e) {
            log.error("Error deleting user avatar", e);
            return DeleteError.INSTANCE;
        }

        enum Success implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
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
            T processDeleteError(DeleteError result);
        }
    }
}

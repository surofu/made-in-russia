package com.surofu.exporteru.core.service.auth.operation;

import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class Logout {
    SecurityUser securityUser;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success() {
            log.info("Successfully processed logout");
            return Success.INSTANCE;
        }

        static Result deleteError(Exception e) {
            log.error("Error processing logout (deleting session)", e);
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

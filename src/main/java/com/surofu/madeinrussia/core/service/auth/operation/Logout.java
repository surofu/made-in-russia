package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class Logout {
    SecurityUser securityUser;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(SimpleResponseMessageDto dto) {
            log.info("Successfully processed logout");
            return Success.of(dto);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            SimpleResponseMessageDto simpleResponseMessageDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }


        interface Processor<T> {
            T processSuccess(Success result);
        }
    }
}

package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.query.me.GetMeQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetMe {
    GetMeQuery query;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserDto userDto) {
            log.info("Successfully processed get me by jwt: {}", userDto);
            return Success.of(userDto);
        }

        static Result sessionIsEmpty() {
            log.warn("Session is empty");
            return SessionIsEmpty.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            UserDto userDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum SessionIsEmpty implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSessionIsEmpty(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processSessionIsEmpty(SessionIsEmpty result);
        }
    }
}

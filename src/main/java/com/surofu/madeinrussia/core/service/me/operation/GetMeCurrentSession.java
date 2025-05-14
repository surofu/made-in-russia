package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.SessionDto;
import com.surofu.madeinrussia.application.query.me.GetMeCurrentSessionQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeCurrentSession {
    GetMeCurrentSessionQuery query;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(SessionDto sessionDto) {
            log.info("Successfully processed get me current session: {}", sessionDto);
            return Success.of(sessionDto);
        }

        static Result sessionIsEmpty() {
            log.warn("Session is empty");
            return SessionIsEmpty.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            SessionDto sessionDto;

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

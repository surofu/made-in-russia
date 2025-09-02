package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.session.SessionDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeCurrentSession {
    SecurityUser securityUser;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(SessionDto sessionDto) {
            log.info("Successfully processed get me current session");
            return Success.of(sessionDto);
        }

        static Result sessionNotFound(Long userId, SessionDeviceId deviceId) {
            log.warn("Session not found: userId={}, deviceId={}", userId, deviceId);
            return SessionNotFound.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            SessionDto sessionDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum SessionNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSessionNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processSessionNotFound(SessionNotFound result);
        }
    }
}

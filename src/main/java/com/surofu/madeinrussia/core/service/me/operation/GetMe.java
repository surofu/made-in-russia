package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
@Value(staticConstructor = "of")
public class GetMe {
    SecurityUser securityUser;
    Locale locale;

    public interface Result {
        static Result success(AbstractAccountDto abstractAccountDto) {
            log.info("Successfully processed get me by jwt");
            return Success.of(abstractAccountDto);
        }

        static Result sessionWithIdNotFound(Long sessionId) {
            log.warn("Session with id '{}' not found", sessionId);
            return SessionWithIdNotFound.INSTANCE;
        }

        static Result sessionWithUserIdAndDeviceIdNotFound(Long userId, SessionDeviceId sessionDeviceId) {
            log.warn("Session not found: userId={}, deviceId={}", userId, sessionDeviceId);
            return SessionWithIdNotFound.INSTANCE;
        }

        <T> T process(Processor<T> processor);

        enum SessionWithIdNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSessionWithIdNotFound(this);
            }
        }

        enum SessionWithUserIdAndDeviceIdNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSessionWithUserIdAndDeviceIdNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processSessionWithIdNotFound(SessionWithIdNotFound result);

            T processSessionWithUserIdAndDeviceIdNotFound(SessionWithUserIdAndDeviceIdNotFound result);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AbstractAccountDto abstractAccountDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }
    }
}

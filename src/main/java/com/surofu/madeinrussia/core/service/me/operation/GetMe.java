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

        static Result sessionWithIdNotFound(Long sessionId) {
            log.warn("Session with id '{}' not found", sessionId);
            return SessionWithIdNotFound.INSTANCE;
        }

        static Result sessionWithUserIdAndDeviceIdNotFound(Long userId, String deviceId) {
            log.warn("Session not found: userId={}, deviceId={}", userId, deviceId);
            return SessionWithIdNotFound.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            UserDto userDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

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
    }
}

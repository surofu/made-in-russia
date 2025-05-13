package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.query.me.GetMeByJwtQuery;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeByJwt {
    GetMeByJwtQuery query;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserDto userDto) {
            log.info("Successfully processed get me by jwt: {}", userDto);
            return Success.of(userDto);
        }

        static Result sessionWithDeviceNotFound(SessionDeviceId sessionDeviceId) {
            log.warn("Session with device id '{}' not found", sessionDeviceId.getDeviceId());
            return SessionWithDeviceNotFound.of(sessionDeviceId);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            UserDto userDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class SessionWithDeviceNotFound implements Result {
            SessionDeviceId sessionDeviceId;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSessionWithDeviceNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processSessionWithDeviceNotFound(SessionWithDeviceNotFound result);
        }
    }
}

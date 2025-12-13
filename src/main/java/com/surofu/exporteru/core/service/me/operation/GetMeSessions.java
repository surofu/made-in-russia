package com.surofu.exporteru.core.service.me.operation;

import com.surofu.exporteru.application.dto.session.SessionDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeSessions {
    SecurityUser securityUser;

    public interface Result {
        static Result success(List<SessionDto> sessionDtos) {
            log.info("Successfully processed get me sessions with size: {}", sessionDtos.size());
            return Success.of(sessionDtos);
        }

        <T> T process(Processor<T> processor);

        interface Processor<T> {
            T processSuccess(Success result);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<SessionDto> sessionDtos;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }
    }
}

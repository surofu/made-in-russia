package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.SessionDto;
import com.surofu.madeinrussia.application.query.me.GetMeSessionsQuery;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class GetMeSessions {
    GetMeSessionsQuery query;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<SessionDto> sessionDtos) {
            log.info("Successfully processed get me sessions with size: {}", sessionDtos.size());
            return Success.of(sessionDtos);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            List<SessionDto> sessionDtos;

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

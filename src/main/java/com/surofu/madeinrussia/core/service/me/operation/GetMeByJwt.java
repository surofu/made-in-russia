package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.query.me.GetMeByJwtQuery;
import com.surofu.madeinrussia.core.model.user.UserEmail;
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

        static Result notFound(UserEmail email) {
            log.warn("User with email '{}' not found.", email.getEmail());
            return NotFound.of(email);
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
        class NotFound implements Result {
            UserEmail email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processNotFound(NotFound result);
        }
    }
}

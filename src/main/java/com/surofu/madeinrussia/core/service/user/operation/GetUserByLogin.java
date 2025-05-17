package com.surofu.madeinrussia.core.service.user.operation;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetUserByLogin {
    UserLogin userLogin;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserDto userDto) {
            log.info("Successfully processed user by login: {}", userDto);
            return Success.of(userDto);
        }

        static Result notFound(UserLogin userLogin) {
            log.warn("User with login '{}' not found", userLogin);
            return NotFound.of(userLogin);
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
            UserLogin userLogin;

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

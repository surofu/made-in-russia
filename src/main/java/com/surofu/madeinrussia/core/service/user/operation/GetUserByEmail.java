package com.surofu.madeinrussia.core.service.user.operation;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetUserByEmail {
    UserEmail userEmail;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(UserDto userDto) {
            log.info("Successfully processed user by email: {}", userDto);
            return Success.of(userDto);
        }

        static Result notFound(UserEmail userEmail) {
            log.warn("User with email '{}' not found", userEmail);
            return NotFound.of(userEmail);
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
            UserEmail userEmail;

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

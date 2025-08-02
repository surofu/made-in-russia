package com.surofu.madeinrussia.core.service.user.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetUserByLogin {
    UserLogin login;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto dto) {
            log.info("Successfully processed user by login: {}", dto.getLogin());
            return Success.of(dto);
        }

        static Result notFound(UserLogin login) {
            log.warn("User with login '{}' not found", login);
            return NotFound.of(login);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AbstractAccountDto dto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            UserLogin login;

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

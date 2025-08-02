package com.surofu.madeinrussia.core.service.user.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class GetUserByEmail {
    UserEmail email;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto dto) {
            log.info("Successfully processed user by email: {}", dto.getEmail());
            return Success.of(dto);
        }

        static Result notFound(UserEmail email) {
            log.warn("User with email '{}' not found", email);
            return NotFound.of(email);
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

package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.command.UpdateMeAccessTokenCommand;
import com.surofu.madeinrussia.application.dto.AccessTokenDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class UpdateMeAccessToken {
    UpdateMeAccessTokenCommand command;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(AccessTokenDto accessTokenDto) {
            log.info("Successfully processed update me access token: {}", accessTokenDto);
            return Success.of(accessTokenDto);
        }

        static Result notFound(String email) {
            log.warn("User with email '{}' not found.", email);
            return NotFound.of(email);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AccessTokenDto accessTokenDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class NotFound implements Result {
            String email;

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

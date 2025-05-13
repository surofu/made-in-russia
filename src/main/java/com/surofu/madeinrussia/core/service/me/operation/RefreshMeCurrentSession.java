package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.command.RefreshMeCurrentSessionCommand;
import com.surofu.madeinrussia.application.dto.TokenDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class RefreshMeCurrentSession {
    RefreshMeCurrentSessionCommand command;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(TokenDto tokenDto) {
            log.info("Successfully processed update me access token: {}", tokenDto);
            return Success.of(tokenDto);
        }

        static Result invalidRefreshToken(String refreshToken, Exception ex) {
            log.warn("Invalid refresh token provided: {}", refreshToken, ex);
            return InvalidRefreshToken.INSTANCE;
        }

        static Result userNotFound(String userEmail) {
            log.warn("User with email '{}' not found", userEmail);
            return UserNotFound.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            TokenDto tokenDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum InvalidRefreshToken implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidRefreshToken(this);
            }
        }

        enum UserNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processInvalidRefreshToken(InvalidRefreshToken result);
            T processUserNotFound(UserNotFound result);
        }
    }
}

package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.TokenDto;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class RefreshMeCurrentSession {
    SessionInfo sessionInfo;
    String refreshToken;

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

        static Result userNotFound(UserEmail userEmail) {
            log.warn("User with email '{}' not found", userEmail);
            return UserNotFound.INSTANCE;
        }

        static Result sessionNotFound(SessionDeviceId deviceId) {
            log.warn("Session with device id '{}' not found", deviceId);
            return SessionNotFound.INSTANCE;
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

        enum SessionNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSessionNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processInvalidRefreshToken(InvalidRefreshToken result);
            T processUserNotFound(UserNotFound result);
            T processSessionNotFound(SessionNotFound result);
        }
    }
}

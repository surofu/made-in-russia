package com.surofu.exporteru.core.service.auth.operation;

import com.surofu.exporteru.application.dto.auth.LoginSuccessDto;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.telegram.TelegramUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class LoginWithTelegram {
    TelegramUser telegramUser;
    SessionInfo sessionInfo;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(LoginSuccessDto dto, String firstName) {
            log.info("Successfully logged in with telegram: {}", firstName);
            return Success.of(dto);
        }

        static Result failure(String firstName) {
            log.info("Failed to log in with telegram: {}", firstName);
            return Failure.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            LoginSuccessDto dto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum Failure implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processFailure(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processFailure(Failure result);
        }
    }
}

package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.command.LoginWithLoginCommand;
import com.surofu.madeinrussia.application.dto.LoginSuccessDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class LoginWithLogin {
    LoginWithLoginCommand command;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(LoginSuccessDto loginSuccessDto) {
            return Success.of(loginSuccessDto);
        }

        static Result invalidCredentials() {
            return InvalidCredentials.INSTANCE;
        }

        static Result notVerified() {
            return NotVerified.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            LoginSuccessDto loginSuccessDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum InvalidCredentials implements Result {
            INSTANCE;


            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidCredentials(this);
            }
        }

        enum NotVerified implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotVerified(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processInvalidCredentials(InvalidCredentials result);
            T processNotVerified(NotVerified result);
        }
    }
}

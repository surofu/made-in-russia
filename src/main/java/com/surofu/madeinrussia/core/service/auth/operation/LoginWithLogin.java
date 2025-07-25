package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.dto.auth.LoginSuccessDto;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class LoginWithLogin {
    UserLogin login;
    UserPasswordPassword password;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(LoginSuccessDto loginSuccessDto) {
            log.info("Successfully processed login with login: {}", loginSuccessDto);
            return Success.of(loginSuccessDto);
        }

        static Result invalidCredentials(UserLogin login, UserPasswordPassword password) {
            log.warn("Invalid login with email credentials provided: Login: {}, Password: {}", login, password);
            return InvalidCredentials.INSTANCE;
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

        interface Processor<T> {
            T processSuccess(Success result);
            T processInvalidCredentials(InvalidCredentials result);
        }
    }
}

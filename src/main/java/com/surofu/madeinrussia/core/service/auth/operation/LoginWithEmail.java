package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.dto.auth.LoginSuccessDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class LoginWithEmail {
    UserEmail email;
    UserPasswordPassword password;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(LoginSuccessDto loginSuccessDto) {
            log.info("Successfully processed login with email");
            return Success.of(loginSuccessDto);
        }

        static Result invalidCredentials() {
            log.warn("Invalid login with email credentials provided");
            return InvalidCredentials.INSTANCE;
        }

        static Result accountBlocked(UserEmail email) {
            log.warn("Account blocked with email: {}", email);
            return AccountBlocked.INSTANCE;
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

        enum AccountBlocked implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processAccountBlocked(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processInvalidCredentials(InvalidCredentials result);
            T processAccountBlocked(AccountBlocked result);
        }
    }
}

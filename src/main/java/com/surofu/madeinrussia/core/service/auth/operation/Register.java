package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.command.RegisterCommand;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class Register {
    RegisterCommand command;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(SimpleResponseMessageDto responseMessageDto) {
            log.info("Successfully processed register user with message: {}", responseMessageDto.getMessage());
            return Success.of(responseMessageDto);
        };

        static Result userWithEmailAlreadyExists(UserEmail email) {
            log.warn("User with email '{}' already exists", email);
            return UserWithEmailAlreadyExists.of(email.getEmail());
        }

        static Result userWithLoginAlreadyExists(UserLogin login) {
            log.warn("User with login '{}' already exists", login);
            return UserWithLoginAlreadyExists.of(login.getLogin());
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            SimpleResponseMessageDto responseMessageDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserWithEmailAlreadyExists implements Result {
            String email;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithEmailAlreadyExists(this);
            }
        }

        @Value(staticConstructor = "of")
        class UserWithLoginAlreadyExists implements Result {
            String login;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserWithLoginAlreadyExists(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processUserWithEmailAlreadyExists(UserWithEmailAlreadyExists result);
            T processUserWithLoginAlreadyExists(UserWithLoginAlreadyExists result);
        }
    }
}

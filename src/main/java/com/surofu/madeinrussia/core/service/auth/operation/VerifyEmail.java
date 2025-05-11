package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.command.VerifyEmailCommand;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class VerifyEmail {
    VerifyEmailCommand command;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(SimpleResponseMessageDto responseMessageDto) {
            log.info("Successfully verified email with message: {}", responseMessageDto.getMessage());
            return Success.of(responseMessageDto);
        }

        static Result accountNotFound(String email) {
            log.warn("Account with email '{}' not found", email);
            return AccountNotFound.INSTANCE;
        }

        static Result invalidVerificationCode(String code) {
            log.warn("Invalid verification code: {}", code);
            return InvalidVerificationCode.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            SimpleResponseMessageDto responseMessageDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum AccountNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processAccountNotFound(this);
            }
        }

        enum InvalidVerificationCode implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidVerificationCode(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processAccountNotFound(AccountNotFound result);
            T processInvalidVerificationCode(InvalidVerificationCode result);
        }
    }
}

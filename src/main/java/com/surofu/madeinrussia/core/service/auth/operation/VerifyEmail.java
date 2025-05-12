package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.command.SaveOrUpdateSessionCommand;
import com.surofu.madeinrussia.application.command.VerifyEmailCommand;
import com.surofu.madeinrussia.application.dto.VerifyEmailSuccessDto;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class VerifyEmail {
    VerifyEmailCommand verifyEmailCommand;
    SaveOrUpdateSessionCommand saveOrUpdateSessionCommand;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(VerifyEmailSuccessDto verifyEmailSuccessDto) {
            log.info("Successfully verified email with message: {}", verifyEmailSuccessDto);
            return Success.of(verifyEmailSuccessDto);
        }

        static Result accountNotFound(String email) {
            log.warn("Account with email '{}' not found", email);
            return AccountNotFound.INSTANCE;
        }

        static Result invalidVerificationCode(String code) {
            log.warn("Invalid verification code: {}", code);
            return InvalidVerificationCode.INSTANCE;
        }

        static Result cacheNotFound(String cacheName) {
            log.error("Cache with name '{}' not found", cacheName);
            return CacheNotFound.of(cacheName);
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            VerifyEmailSuccessDto verifyEmailSuccessDto;

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

        @Value(staticConstructor = "of")
        class CacheNotFound implements Result {
            String cacheName;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processCacheNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processAccountNotFound(AccountNotFound result);
            T processInvalidVerificationCode(InvalidVerificationCode result);
            T processCacheNotFound(CacheNotFound result);
        }
    }
}

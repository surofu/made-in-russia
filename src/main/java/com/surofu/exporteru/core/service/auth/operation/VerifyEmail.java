package com.surofu.exporteru.core.service.auth.operation;

import com.surofu.exporteru.application.dto.auth.VerifyEmailSuccessDto;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.auth.VerificationCode;
import com.surofu.exporteru.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class VerifyEmail {
    UserEmail userEmail;
    VerificationCode verificationCode;
    SessionInfo sessionInfo;

    public interface Result {
        <T> T process(Processor<T> processor);

        static Result success(VerifyEmailSuccessDto verifyEmailSuccessDto) {
            log.info("Successfully verified email");
            return Success.of(verifyEmailSuccessDto);
        }

        static Result accountNotFound(UserEmail userEmail) {
            log.warn("Account with email '{}' not found", userEmail.toString());
            return AccountNotFound.of(userEmail);
        }

        static Result invalidVerificationCode(VerificationCode verificationCode) {
            log.warn("Invalid verification code: {}", verificationCode.toString());
            return InvalidVerificationCode.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.warn("Translation error: {}", e.getMessage());
            return TranslationError.INSTANCE;
        }

        static Result saveError(UserEmail userEmail, Exception e) {
            log.warn("Saving error while verify email: {}", userEmail.toString(), e);
            return SaveError.INSTANCE;
        }

        static Result saveSessionError(UserEmail userEmail, Exception e) {
            log.warn("Saving session error while verify email: {}", userEmail.toString(), e);
            return SaveSessionError.INSTANCE;
        }

        static Result outOfAttempts(UserEmail userEmail) {
            log.warn("Out of attempts: {}", userEmail.toString());
            return OutOfAttempts.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            VerifyEmailSuccessDto verifyEmailSuccessDto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        @Value(staticConstructor = "of")
        class AccountNotFound implements Result {
            UserEmail userEmail;

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

        enum TranslationError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processTranslationError(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        enum SaveSessionError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveSessionError(this);
            }
        }

        enum OutOfAttempts implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processOutOfAttempts(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);
            T processAccountNotFound(AccountNotFound result);
            T processInvalidVerificationCode(InvalidVerificationCode result);
            T processTranslationError(TranslationError result);
            T processSaveError(SaveError result);
            T processSaveSessionError(SaveSessionError result);
            T processOutOfAttempts(OutOfAttempts result);
        }
    }
}

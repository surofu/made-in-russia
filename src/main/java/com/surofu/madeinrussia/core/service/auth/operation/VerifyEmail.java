package com.surofu.madeinrussia.core.service.auth.operation;

import com.surofu.madeinrussia.application.dto.VerifyEmailSuccessDto;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value(staticConstructor = "of")
public class VerifyEmail {
    UserEmail userEmail;
    String verificationCode;
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

        static Result invalidVerificationCode(String verificationCode) {
            log.warn("Invalid verification code: {}", verificationCode);
            return InvalidVerificationCode.INSTANCE;
        }

        static Result translationError(Exception e) {
            log.warn("Translation error: {}", e.getMessage());
            return TranslationError.INSTANCE;
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

        interface Processor<T> {
            T processSuccess(Success result);
            T processAccountNotFound(AccountNotFound result);
            T processInvalidVerificationCode(InvalidVerificationCode result);
            T processTranslationError(TranslationError result);
        }
    }
}

package com.surofu.madeinrussia.infrastructure.web.mapper.support;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.support.operation.SendSupportMail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendSupportMailMapperResultToResponseEntity
implements SendSupportMail.Result.Processor<ResponseEntity<?>> {
    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(SendSupportMail.Result.Success result) {
        String message = localizationManager.localize("mail.support.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processSendEmailError(SendSupportMail.Result.SendEmailError result) {
        String message = localizationManager.localize("mail.send_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processTranslationError(SendSupportMail.Result.TranslationError result) {
        String message = localizationManager.localize("translation.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

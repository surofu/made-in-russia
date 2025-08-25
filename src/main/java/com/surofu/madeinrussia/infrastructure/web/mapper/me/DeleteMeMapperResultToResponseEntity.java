package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.me.operation.DeleteMe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteMeMapperResultToResponseEntity
implements DeleteMe.Result.Processor<ResponseEntity<?>> {
    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(DeleteMe.Result.Success result) {
        String message = localizationManager.localize("account.mail.delete_confirmation_mail", result.getEmail().toString());
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processSendMailError(DeleteMe.Result.SendMailError result) {
        String message = localizationManager.localize("mail.send_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

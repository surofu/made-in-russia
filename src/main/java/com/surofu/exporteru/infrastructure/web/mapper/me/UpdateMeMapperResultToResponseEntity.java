package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.me.operation.UpdateMe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateMeMapperResultToResponseEntity
        implements UpdateMe.Result.Processor<ResponseEntity<?>> {
    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(UpdateMe.Result.Success result) {
        return new ResponseEntity<>(result.getAccountDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processTranslationError(UpdateMe.Result.TranslationError result) {
        String message = localizationManager.localize("translation.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processPhoneNumberAlreadyExists(UpdateMe.Result.PhoneNumberAlreadyExists result) {
        String message = localizationManager.localize("auth.register.user_with_phone_already_exists", result.getPhoneNumber());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processInnAlreadyExists(UpdateMe.Result.InnAlreadyExists result) {
        String message = localizationManager.localize("auth.register.vendor_with_inn_already_exists", result.getInn());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processSaveError(UpdateMe.Result.SaveError result) {
        String message = localizationManager.localize("user.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

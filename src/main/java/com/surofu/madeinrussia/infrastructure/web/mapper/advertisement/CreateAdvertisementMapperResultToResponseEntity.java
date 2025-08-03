package com.surofu.madeinrussia.infrastructure.web.mapper.advertisement;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.advertisement.operation.CreateAdvertisement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAdvertisementMapperResultToResponseEntity
implements CreateAdvertisement.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(CreateAdvertisement.Result.Success result) {
        String message = localizationManager.localize("advertisement.save_success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processTranslationError(CreateAdvertisement.Result.TranslationError result) {
        String message = localizationManager.localize("translation.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSavingFileError(CreateAdvertisement.Result.SavingFileError result) {
        String message = localizationManager.localize("advertisement.save_file_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processEmptyTransaction(CreateAdvertisement.Result.EmptyTransaction result) {
        String message = localizationManager.localize("translation.empty_translations");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processSavingAdvertisementError(CreateAdvertisement.Result.SavingAdvertisementError result) {
        String message = localizationManager.localize("advertisement.save_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processEmptyFile(CreateAdvertisement.Result.EmptyFile result) {
        String message = localizationManager.localize("advertisement.empty_file");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processDeletingFileError(CreateAdvertisement.Result.DeletingFileError result) {
        String message = localizationManager.localize("advertisement.delete_file_error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

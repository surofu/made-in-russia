package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.me.operation.UploadMeVendorMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UploadMeVendorMediaMapperResultToResponseEntity
        implements UploadMeVendorMedia.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(UploadMeVendorMedia.Result.Success result) {
        return new ResponseEntity<>(result.getDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processEmptyFile(UploadMeVendorMedia.Result.EmptyFile result) {
        String message = localizationManager.localize("file_storage.empty_file");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processUnknownContentType(UploadMeVendorMedia.Result.UnknownContentType result) {
        String message = localizationManager.localize("file_storage.empty_file");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processUploadError(UploadMeVendorMedia.Result.UploadError result) {
        String message = localizationManager.localize("file_storage.upload.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processSaveError(UploadMeVendorMedia.Result.SaveError result) {
        String message = localizationManager.localize("user.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processInvalidPosition(UploadMeVendorMedia.Result.InvalidPosition result) {
        String message = localizationManager.localize("vendor.media.invalid_position");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}

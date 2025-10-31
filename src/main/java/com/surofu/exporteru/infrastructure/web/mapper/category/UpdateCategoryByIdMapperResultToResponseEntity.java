package com.surofu.exporteru.infrastructure.web.mapper.category;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.category.operation.UpdateCategoryById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateCategoryByIdMapperResultToResponseEntity
        implements UpdateCategoryById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(UpdateCategoryById.Result.Success result) {
        String message = localizationManager.localize("category.save.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> processNotFound(UpdateCategoryById.Result.NotFound result) {
        String message = localizationManager.localize("category.not_found_by_id", result.getId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processSlugAlreadyExists(UpdateCategoryById.Result.SlugAlreadyExists result) {
        String message = localizationManager.localize("category.slug.already_exists", result.getSlug().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> processParentNotFound(UpdateCategoryById.Result.ParentNotFound result) {
        String message = localizationManager.localize("category.parent_not_found_by_id", result.getId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processSaveError(UpdateCategoryById.Result.SaveError result) {
        String message = localizationManager.localize("category.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processParentSlugLevelParseError(UpdateCategoryById.Result.ParentSlugLevelParseError result) {
        String message = localizationManager.localize("category.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processEmptyTranslations(UpdateCategoryById.Result.EmptyTranslations result) {
        String message = localizationManager.localize("translation.empty");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processTranslationError(UpdateCategoryById.Result.TranslationError result) {
        String message = localizationManager.localize("translation.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processDeleteMediaError(UpdateCategoryById.Result.DeleteMediaError result) {
        String message = localizationManager.localize("file_storage.delete.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processUploadImageError(UpdateCategoryById.Result.UploadImageError result) {
        String message = localizationManager.localize("file_storage.upload.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

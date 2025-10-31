package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.me.operation.DeleteMeReviewById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteMeReviewByIdMapperResultToResponseEntity
        implements DeleteMeReviewById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(DeleteMeReviewById.Result.Success result) {
        String message = localizationManager.localize("product.review.delete.success");
        SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(DeleteMeReviewById.Result.NotFound result) {
        String message = localizationManager.localize("product.review.not_found_by_id", result.getId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processDeleteError(DeleteMeReviewById.Result.DeleteError result) {
        String message = localizationManager.localize("product.review.delete.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

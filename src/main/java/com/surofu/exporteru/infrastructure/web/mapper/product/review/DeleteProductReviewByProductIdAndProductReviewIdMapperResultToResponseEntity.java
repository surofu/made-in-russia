package com.surofu.exporteru.infrastructure.web.mapper.product.review;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.product.review.operation.DeleteProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteProductReviewByProductIdAndProductReviewIdMapperResultToResponseEntity
        implements DeleteProductReview.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(DeleteProductReview.Result.Success result) {
        String message = localizationManager.localize("product.review.delete.success");
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processProductReviewNotFound(DeleteProductReview.Result.ProductReviewNotFound result) {
        String message = localizationManager.localize("product.review.not_found_by_id_and_product_id", result.getProductReviewId(), result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processForbidden(DeleteProductReview.Result.Forbidden result) {
        String message = localizationManager.localize("product.review.error.not_owner");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processUnauthorized(DeleteProductReview.Result.Unauthorized result) {
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> processDeleteError(DeleteProductReview.Result.DeleteError result) {
        String message = localizationManager.localize("product.review.delete.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processDeleteMediaError(DeleteProductReview.Result.DeleteMediaError result) {
        String message = localizationManager.localize("file_storage.delete.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.surofu.madeinrussia.infrastructure.web.mapper.product.review;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.product.review.operation.UpdateProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateProductReviewByProductIdAndProductReviewIdMapperResultToResponseEntity
implements UpdateProductReview.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(UpdateProductReview.Result.Success result) {
        String message = localizationManager.localize("product.review.update.success");
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processProductReviewNotFound(UpdateProductReview.Result.ProductReviewNotFound result) {
        String message = localizationManager.localize("product.review.not_found_by_id_and_product_id", result.getProductReviewId(), result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processForbidden(UpdateProductReview.Result.Forbidden result) {
        String message = localizationManager.localize("product.review.error.not_owner");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processUnauthorized(UpdateProductReview.Result.Unauthorized result) {
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<?> processSaveError(UpdateProductReview.Result.SaveError result) {
        String message = localizationManager.localize("product.review.save.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processTranslationError(UpdateProductReview.Result.TranslationError result) {
        String message = localizationManager.localize("translation.error");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

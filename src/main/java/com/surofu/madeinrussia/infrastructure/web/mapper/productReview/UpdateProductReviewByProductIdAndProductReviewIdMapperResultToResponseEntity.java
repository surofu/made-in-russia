package com.surofu.madeinrussia.infrastructure.web.mapper.productReview;

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
        String message = localizationManager.localize("product_review.update.success");
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processProductReviewNotFound(UpdateProductReview.Result.ProductReviewNotFound result) {
        String message = localizationManager.localize("product_review.not_found_by_id", result.getProductReviewId(), result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processForbidden(UpdateProductReview.Result.Forbidden result) {
        String message = localizationManager.localize("product_review.error.not_owner");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processUnauthorized(UpdateProductReview.Result.Unauthorized result) {
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }
}

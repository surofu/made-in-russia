package com.surofu.madeinrussia.infrastructure.web.mapper.productReview;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.product.review.operation.CreateProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateProductReviewMapperResultToResponseEntity
implements CreateProductReview.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(CreateProductReview.Result.Success result) {
        String message = localizationManager.localize("product_review.create.success");
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> processProductNotFound(CreateProductReview.Result.ProductNotFound result) {
        String message = localizationManager.localize("product.not_found_by_id", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processVendorProfileNotViewed(CreateProductReview.Result.VendorProfileNotViewed result) {
        String message = localizationManager.localize("product_review.error.order_before_create");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processAccountIsTooYoung(CreateProductReview.Result.AccountIsTooYoung result) {
        String message = localizationManager.localize("product_review.error.minimum_account_age");
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }
}

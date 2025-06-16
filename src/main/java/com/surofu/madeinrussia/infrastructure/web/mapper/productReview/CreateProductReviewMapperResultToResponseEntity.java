package com.surofu.madeinrussia.infrastructure.web.mapper.productReview;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.productReview.operation.CreateProductReview;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateProductReviewMapperResultToResponseEntity
implements CreateProductReview.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(CreateProductReview.Result.Success result) {
        String message = "Отзыв был успешно создан";
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> processProductNotFound(CreateProductReview.Result.ProductNotFound result) {
        String message = String.format("Товар с ID '%s' не найден", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processUnauthorized(CreateProductReview.Result.Unauthorized result) {
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }
}

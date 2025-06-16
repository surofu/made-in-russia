package com.surofu.madeinrussia.infrastructure.web.mapper.productReview;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.productReview.operation.UpdateProductReview;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductReviewByProductIdAndProductReviewIdMapperResultToResponseEntity
implements UpdateProductReview.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(UpdateProductReview.Result.Success result) {
        String message = "Отзыв был успешно обновлен";
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processProductReviewNotFound(UpdateProductReview.Result.ProductReviewNotFound result) {
        String message = String.format("Отзыв с ID '%s' в товаре с ID '%s' не найден", result.getProductReviewId(), result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processForbidden(UpdateProductReview.Result.Forbidden result) {
        String message = "Нет доступа. Вы не являетесь автором отзыва";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processUnauthorized(UpdateProductReview.Result.Unauthorized result) {
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }
}

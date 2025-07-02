package com.surofu.madeinrussia.infrastructure.web.mapper.productReview;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.review.operation.DeleteProductReview;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DeleteProductReviewByProductIdAndProductReviewIdMapperResultToResponseEntity
        implements DeleteProductReview.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(DeleteProductReview.Result.Success result) {
        String message = "Отзыв был успешно удален";
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processProductReviewNotFound(DeleteProductReview.Result.ProductReviewNotFound result) {
        String message = String.format("Отзыв с ID '%s' в товаре с ID '%s' не найден", result.getProductReviewId(), result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processForbidden(DeleteProductReview.Result.Forbidden result) {
        String message = "Нет доступа. Вы не являетесь автором отзыва";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> processUnauthorized(DeleteProductReview.Result.Unauthorized result) {
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }
}

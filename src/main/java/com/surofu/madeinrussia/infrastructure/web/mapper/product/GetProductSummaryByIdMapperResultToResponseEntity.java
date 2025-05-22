package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductSummaryByIdMapperResultToResponseEntity implements GetProductSummaryViewById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductSummaryViewById.Result.Success result) {
        return new ResponseEntity<>(result.getProductSummaryViewDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductSummaryViewById.Result.NotFound result) {
        String message = String.format("Продукт с ID '%s' не найден", result.getProductSummaryId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

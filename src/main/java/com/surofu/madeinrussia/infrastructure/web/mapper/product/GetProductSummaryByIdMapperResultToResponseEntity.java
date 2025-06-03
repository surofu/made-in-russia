package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.error.ProductNotFoundByIdResponseErrorDto;
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
       return new ResponseEntity<>(ProductNotFoundByIdResponseErrorDto.of(result.getProductSummaryId()), HttpStatus.NOT_FOUND);
    }
}

package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.error.ProductNotFoundByIdResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.GetProductCharacteristicsByProductId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductCharacteristicsByProductIdMapperResultToResponseEntity
        implements GetProductCharacteristicsByProductId.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductCharacteristicsByProductId.Result.Success result) {
        return new ResponseEntity<>(result.getProductCharacteristicDtos(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductCharacteristicsByProductId.Result.NotFound result) {
        return new ResponseEntity<>(ProductNotFoundByIdResponseErrorDto.of(result.getProductId()), HttpStatus.NOT_FOUND);
    }
}

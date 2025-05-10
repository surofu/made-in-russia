package com.surofu.madeinrussia.infrastructure.web.mapper.deliveryMethod;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDeliveryMethodByIdMapperResultToResponseEntity implements GetDeliveryMethodById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetDeliveryMethodById.Result.Success result) {
        return new ResponseEntity<>(result.getDeliveryMethodDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetDeliveryMethodById.Result.NotFound result) {
        String errorMessage = String.format( "Product with ID '%s' not found", result.getDeliveryMethodId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

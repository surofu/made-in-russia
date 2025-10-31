package com.surofu.exporteru.infrastructure.web.mapper.deliveryMethod;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetDeliveryMethodByIdMapperResultToResponseEntity
        implements GetDeliveryMethodById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetDeliveryMethodById.Result.Success result) {
        return new ResponseEntity<>(result.getDeliveryMethodDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetDeliveryMethodById.Result.NotFound result) {
        String errorMessage = localizationManager.localize("delivery_method.not_found_by_id", result.getDeliveryMethodId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

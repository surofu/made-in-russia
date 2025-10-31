package com.surofu.exporteru.infrastructure.web.mapper.deliveryMethod;

import com.surofu.exporteru.core.service.deliveryMethod.operation.GetDeliveryMethods;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDeliveryMethodsMapperResultToResponseEntity implements GetDeliveryMethods.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetDeliveryMethods.Result.Success result) {
        return new ResponseEntity<>(result.getDeliveryMethodDtos(), HttpStatus.OK);
    }
}

package com.surofu.exporteru.infrastructure.web.mapper.deliveryTerm;

import com.surofu.exporteru.core.service.deliveryTerm.operation.GetAllDeliveryTerms;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetAllDeliveryTermsMapperResultToResponseEntity
    implements GetAllDeliveryTerms.Result.Processor<ResponseEntity<?>> {

  @Override
  public ResponseEntity<?> processSuccess(GetAllDeliveryTerms.Result.Success result) {
    return new ResponseEntity<>(result.getDtos(), HttpStatus.OK);
  }
}

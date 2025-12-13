package com.surofu.exporteru.infrastructure.web.mapper.product;

import com.surofu.exporteru.core.service.product.operation.GetProductSummaryViewsByIds;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductSummaryViewsByIdsMapperResultToResponseEntity
implements GetProductSummaryViewsByIds.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductSummaryViewsByIds.Result.Success result) {
        return new ResponseEntity<>(result.getProductSummaryViewDtos(), HttpStatus.OK);
    }
}

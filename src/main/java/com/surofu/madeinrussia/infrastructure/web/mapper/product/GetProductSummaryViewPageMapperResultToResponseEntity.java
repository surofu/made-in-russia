package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductSummaryViewPageMapperResultToResponseEntity implements GetProductSummaryViewPage.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProductSummaryViewPage.Result.Success result) {
        return new ResponseEntity<>(result.getProductSummaryViewDtoPage(), HttpStatus.OK);
    }
}

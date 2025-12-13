package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.core.service.me.operation.GetMeVendorProductReviewPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeVendorProductReviewPageMapperResultToResponseEntity
        implements GetMeVendorProductReviewPage.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeVendorProductReviewPage.Result.Success result) {
        return new ResponseEntity<>(result.getVendorProductReviewDtoPage(), HttpStatus.OK);
    }
}

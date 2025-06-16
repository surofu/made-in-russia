package com.surofu.madeinrussia.infrastructure.web.mapper.vendor;

import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetVendorReviewPageByVendorIdMapperResultToResponseEntity
implements GetVendorReviewPageById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetVendorReviewPageById.Result.Success result) {
        return new ResponseEntity<>(result.getVendorReviewPageDto(), HttpStatus.OK);
    }
}

package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.core.service.me.operation.GetMeReviewPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeReviewPageMapperResultToResponseEntity implements GetMeReviewPage.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeReviewPage.Result.Success result) {
        return new ResponseEntity<>(result.getProductReviewDtoPage(), HttpStatus.OK);
    }
}

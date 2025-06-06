package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.core.service.me.operation.GetMeProductSummaryViewPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeProductSummaryViewPageMapperResultToResponseEntity
        implements GetMeProductSummaryViewPage.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeProductSummaryViewPage.Result.Success result) {
        return new ResponseEntity<>(result.getProductSummaryViewDtoPage(), HttpStatus.OK);
    }
}

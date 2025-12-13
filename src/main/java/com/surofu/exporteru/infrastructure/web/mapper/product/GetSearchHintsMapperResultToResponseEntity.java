package com.surofu.exporteru.infrastructure.web.mapper.product;

import com.surofu.exporteru.core.service.product.operation.GetSearchHints;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetSearchHintsMapperResultToResponseEntity
        implements GetSearchHints.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetSearchHints.Result.Success result) {
        return new ResponseEntity<>(result.getSearchHints(), HttpStatus.OK);
    }
}

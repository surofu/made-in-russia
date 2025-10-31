package com.surofu.exporteru.infrastructure.web.mapper.seo;

import com.surofu.exporteru.core.service.seo.operation.GetSeo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetSeoMapperResultToResponseEntity
        implements GetSeo.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetSeo.Result.Success result) {
        return new ResponseEntity<>(result.getSeo(), HttpStatus.OK);
    }
}

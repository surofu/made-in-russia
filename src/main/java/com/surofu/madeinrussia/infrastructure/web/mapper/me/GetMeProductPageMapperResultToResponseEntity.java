package com.surofu.madeinrussia.infrastructure.web.mapper.me;

import com.surofu.madeinrussia.core.service.me.operation.GetMeProductPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeProductPageMapperResultToResponseEntity
        implements GetMeProductPage.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeProductPage.Result.Success result) {
        return new ResponseEntity<>(result.getProductDtoPage(), HttpStatus.OK);
    }
}

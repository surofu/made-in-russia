package com.surofu.exporteru.infrastructure.web.mapper.me;

import com.surofu.exporteru.core.service.me.operation.GetMeFavoriteProducts;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetMeFavoriteProductsMapperResultToResponseEntity
        implements GetMeFavoriteProducts.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetMeFavoriteProducts.Result.Success result) {
        return new ResponseEntity<>(result.getProducts(), HttpStatus.OK);
    }
}

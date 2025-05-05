package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.core.service.product.operation.GetProducts;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProductsMapperResultToResponseEntity implements GetProducts.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetProducts.Result.Success result) {
        return new ResponseEntity<>(result.getProductDtoPage(), HttpStatus.OK);
    }
}

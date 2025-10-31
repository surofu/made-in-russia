package com.surofu.exporteru.infrastructure.web.mapper.category;

import com.surofu.exporteru.core.service.category.operation.GetCategories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetCategoriesMapperResultToResponseEntity implements GetCategories.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetCategories.Result.Success result) {
        return new ResponseEntity<>(result.getCategoryDtos(), HttpStatus.OK);
    }
}

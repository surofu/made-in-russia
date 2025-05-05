package com.surofu.madeinrussia.infrastructure.web.mapper.category;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetCategoryByIdMapperResultToResponseEntity implements GetCategoryById.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(GetCategoryById.Result.Success result) {
        return new ResponseEntity<>(result.getCategoryDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetCategoryById.Result.NotFound result) {
        String errorMessage = "Category with id '%d' not found".formatted(result.getCategoryId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

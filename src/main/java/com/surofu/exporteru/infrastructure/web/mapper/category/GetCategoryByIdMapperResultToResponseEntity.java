package com.surofu.exporteru.infrastructure.web.mapper.category;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.category.operation.GetCategoryById;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCategoryByIdMapperResultToResponseEntity
        implements GetCategoryById.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetCategoryById.Result.Success result) {
        return new ResponseEntity<>(result.getCategoryDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetCategoryById.Result.NotFound result) {
        String errorMessage = localizationManager.localize("category.not_found_by_id", result.getCategoryId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

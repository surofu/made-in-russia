package com.surofu.madeinrussia.infrastructure.web.mapper.category;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryBySlug;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCategoryBySlugMapperResultToResponseEntity
        implements GetCategoryBySlug.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetCategoryBySlug.Result.Success result) {
        return new ResponseEntity<>(result.getCategoryDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetCategoryBySlug.Result.NotFound result) {
        String errorMessage = localizationManager.localize("category.not_found_by_slug", result.getCategorySlug().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(errorMessage, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

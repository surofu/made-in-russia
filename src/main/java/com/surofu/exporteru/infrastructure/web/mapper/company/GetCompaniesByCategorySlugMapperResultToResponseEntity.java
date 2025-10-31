package com.surofu.exporteru.infrastructure.web.mapper.company;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.company.operation.GetCompaniesByCategorySlug;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCompaniesByCategorySlugMapperResultToResponseEntity
implements GetCompaniesByCategorySlug.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetCompaniesByCategorySlug.Result.Success result) {
        return new ResponseEntity<>(result.getOkvedCompanyList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetCompaniesByCategorySlug.Result.NotFound result) {
        String message = localizationManager.localize("category.not_found_by_slug",
                result.getCategorySlug().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

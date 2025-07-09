package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.service.product.operation.GetProductByArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetProductByArticleMapperResultToResponseEntity
        implements GetProductByArticle.Result.Processor<ResponseEntity<?>> {

    private final LocalizationManager localizationManager;

    @Override
    public ResponseEntity<?> processSuccess(GetProductByArticle.Result.Success result) {
        return new ResponseEntity<>(result.getProductDto(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processNotFound(GetProductByArticle.Result.NotFound result) {
        String message = localizationManager.localize("product.not_found_by_article", result.getArticleCode().toString());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}

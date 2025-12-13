package com.surofu.exporteru.infrastructure.web.mapper.product;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.product.operation.GetSimilarProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetSimilarProductsMapperResultToResponseEntity
implements GetSimilarProducts.Result.Processor<ResponseEntity<?>> {
  private final LocalizationManager localizationManager;

  @Override
  public ResponseEntity<?> processSuccess(GetSimilarProducts.Result.Success result) {
    return new ResponseEntity<>(result.getProducts(), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> processNotFound(GetSimilarProducts.Result.NotFound result) {
    String message = localizationManager.localize("product.not_found_by_id", result.getProductId());
    SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
  }
}

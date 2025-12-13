package com.surofu.exporteru.infrastructure.web.mapper.product;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.product.operation.UpdateProductOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateProductOwnerMapperResultToResponseEntity
    implements UpdateProductOwner.Result.Processor<ResponseEntity<?>> {
  private final LocalizationManager localizationManager;

  @Override
  public ResponseEntity<?> processSuccess(UpdateProductOwner.Result.Success result) {
    String message = localizationManager.localize("product.success_update_owner");
    SimpleResponseMessageDto messageDto = SimpleResponseMessageDto.of(message);
    return new ResponseEntity<>(messageDto, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> processProductNotFound(
      UpdateProductOwner.Result.ProductNotFound result) {
    String message = localizationManager.localize("product.not_found_by_id", result.getProductId());
    SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
  }

  @Override
  public ResponseEntity<?> processUserNotFound(UpdateProductOwner.Result.UserNotFound result) {
    String message = localizationManager.localize("user.not_found_by_id", result.getUserId());
    SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
  }

  @Override
  public ResponseEntity<?> processUserNotVendor(UpdateProductOwner.Result.UserNotVendor result) {
    String message = localizationManager.localize("user.not_vendor");
    SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }

  @Override
  public ResponseEntity<?> processSaveError(UpdateProductOwner.Result.SaveError result) {
    String message = localizationManager.localize("product.error.save_product_data");
    SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

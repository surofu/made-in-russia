package com.surofu.exporteru.infrastructure.web.mapper.deliveryTerm;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.deliveryTerm.operation.DeleteDeliveryTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteDeliveryTermMapperResultToResponseEntity
implements DeleteDeliveryTerm.Result.Processor<ResponseEntity<?>> {
  private final LocalizationManager localizationManager;

  @Override
  public ResponseEntity<?> processSuccess(DeleteDeliveryTerm.Result.Success result) {
    String message = localizationManager.localize("delivery_term.delete.success");
    SimpleResponseMessageDto dto = SimpleResponseMessageDto.of(message);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> processNotFound(DeleteDeliveryTerm.Result.NotFound result) {
    String message = localizationManager.localize("delivery_term.not_found_by_id", result.getId());
    SimpleResponseErrorDto dto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(dto, HttpStatus.NOT_FOUND);
  }

  @Override
  public ResponseEntity<?> processDeleteError(DeleteDeliveryTerm.Result.DeleteError result) {
    String message = localizationManager.localize("delivery_term.delete.error");
    SimpleResponseErrorDto dto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

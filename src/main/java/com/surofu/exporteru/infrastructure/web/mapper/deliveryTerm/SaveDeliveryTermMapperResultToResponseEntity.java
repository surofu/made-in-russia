package com.surofu.exporteru.infrastructure.web.mapper.deliveryTerm;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.service.deliveryTerm.operation.SaveDeliveryTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveDeliveryTermMapperResultToResponseEntity
    implements SaveDeliveryTerm.Result.Processor<ResponseEntity<?>> {
  private final LocalizationManager localizationManager;

  @Override
  public ResponseEntity<?> processSuccess(SaveDeliveryTerm.Result.Success result) {
    String message = localizationManager.localize("delivery_term.save.success");
    SimpleResponseMessageDto dto = SimpleResponseMessageDto.of(message);
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> processNotFound(SaveDeliveryTerm.Result.NotFound result) {
    String message =
        localizationManager.localize("delivery_term.not_found_by_id", result.getId());
    SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
  }

  @Override
  public ResponseEntity<?> processAlreadyExistWithCode(
      SaveDeliveryTerm.Result.AlreadyExistWithCode result) {
    String message = localizationManager.localize("validation.delivery_term.code.already_exists",
        result.getCode().getValue());
    SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.CONFLICT);
    return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
  }

  @Override
  public ResponseEntity<?> processSaveError(SaveDeliveryTerm.Result.SaveError result) {
    String message = localizationManager.localize("delivery_term.save.error");
    SimpleResponseErrorDto errorDto =
        SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

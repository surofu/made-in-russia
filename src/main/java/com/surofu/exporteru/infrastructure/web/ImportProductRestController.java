package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.importproduct.ImportProductCommand;
import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.service.product.ImportProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/import/products")
public class ImportProductRestController {
  private final ImportProductUseCase importProductUseCase;

  @PostMapping
  public ResponseEntity<?> importProduct(@RequestBody ImportProductCommand command) {
    try {
      importProductUseCase.execute(command);
    } catch (Exception exception) {
      log.error(exception.getMessage(), exception);
      return new ResponseEntity<>(
          SimpleResponseErrorDto.of(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return ResponseEntity.ok().body(SimpleResponseMessageDto.of("Товар успешно импортирован"));
  }

}

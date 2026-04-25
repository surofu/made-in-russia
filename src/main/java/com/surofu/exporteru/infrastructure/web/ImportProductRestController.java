package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.importproduct.ImportProductCommand;
import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.service.product.ImportProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/import/products")
public class ImportProductRestController {
  private final ImportProductUseCase importProductUseCase;

  @PostMapping
  public ResponseEntity<?> importProduct(@RequestBody ImportProductCommand command) {
    importProductUseCase.execute(command);
    return ResponseEntity.ok().body(SimpleResponseMessageDto.of("Товар успешно импортирован"));
  }

}

package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.deliveryTerm.SaveDeliveryTermCommand;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermCode;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermDescription;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTermName;
import com.surofu.exporteru.core.service.deliveryTerm.DeliveryTermService;
import com.surofu.exporteru.core.service.deliveryTerm.operation.DeleteDeliveryTerm;
import com.surofu.exporteru.core.service.deliveryTerm.operation.GetAllDeliveryTerms;
import com.surofu.exporteru.core.service.deliveryTerm.operation.SaveDeliveryTerm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery-terms")
@Tag(name = "Delivery Term")
public class DeliveryTermController {
  private final DeliveryTermService service;
  private final GetAllDeliveryTerms.Result.Processor<ResponseEntity<?>>
      getAllDeliveryTermsProcessor;
  private final SaveDeliveryTerm.Result.Processor<ResponseEntity<?>> saveDeliveryTermsProcessor;
  private final DeleteDeliveryTerm.Result.Processor<ResponseEntity<?>> deleteDeliveryTermsProcessor;

  @GetMapping
  @Operation(summary = "Get all delivery-terms")
  public ResponseEntity<?> getAll() {
    return service.getAll().process(getAllDeliveryTermsProcessor);
  }

  @PutMapping
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Create or update delivery-term")
  public ResponseEntity<?> save(@RequestBody SaveDeliveryTermCommand command) {
    SaveDeliveryTerm operation = SaveDeliveryTerm.of(
        command.id(),
        new DeliveryTermCode(command.code()),
        new DeliveryTermName(command.name(), new HashMap<>()),
        new DeliveryTermDescription(command.description(), new HashMap<>())
    );
    return service.save(operation).process(saveDeliveryTermsProcessor);
  }

  @DeleteMapping("{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Delete delivery-term by ID")
  public ResponseEntity<?> delete(@PathVariable long id) {
    DeleteDeliveryTerm operation = DeleteDeliveryTerm.of(id);
    return service.delete(operation).process(deleteDeliveryTermsProcessor);
  }
}

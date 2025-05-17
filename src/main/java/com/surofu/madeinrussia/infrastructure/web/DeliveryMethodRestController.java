package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.core.service.deliveryMethod.DeliveryMethodService;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethodById;
import com.surofu.madeinrussia.core.service.deliveryMethod.operation.GetDeliveryMethods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/delivery-methods")
@Tag(
        name = "Delivery Methods",
        description = "API for managing product delivery methods"
)
public class DeliveryMethodRestController {
    private final DeliveryMethodService deliveryMethodService;
    private final GetDeliveryMethods.Result.Processor<ResponseEntity<?>> getDeliveryMethodsProcessor;
    private final GetDeliveryMethodById.Result.Processor<ResponseEntity<?>> getDeliveryMethodByIdProcessor;

    @GetMapping
    @Operation(
            summary = "Get all delivery methods",
            description = "Retrieves a list of all available delivery methods",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved delivery methods",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DeliveryMethodDto.class))
                            )
                    )
            }
    )
    public ResponseEntity<?> getDeliveryMethods() {
        return deliveryMethodService.getDeliveryMethods().process(getDeliveryMethodsProcessor);
    }

    @GetMapping("/{deliveryMethodId}")
    @Operation(
            summary = "Get delivery method by ID",
            description = "Retrieves a specific delivery method by its unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Delivery method found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DeliveryMethodDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Delivery method not found",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<?> getDeliveryMethodById(
            @Parameter(
                    name = "id",
                    description = "ID of the delivery method to retrieve",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable
            Long deliveryMethodId
    ) {
        GetDeliveryMethodById operation = GetDeliveryMethodById.of(deliveryMethodId);
        return deliveryMethodService.getDeliveryMethodById(operation).process(getDeliveryMethodByIdProcessor);
    }
}

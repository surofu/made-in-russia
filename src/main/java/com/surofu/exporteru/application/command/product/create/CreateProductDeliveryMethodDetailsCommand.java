package com.surofu.exporteru.application.command.product.create;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Command for creating product delivery method details")
public record CreateProductDeliveryMethodDetailsCommand(
    @Schema(description = "Name of the delivery method detail",
        example = "Delivery Time",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    Map<String, String> nameTranslations,

    @Schema(description = "Value of the delivery method detail",
        example = "2-3 business days",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String value,

    Map<String, String> valueTranslations
) {
}

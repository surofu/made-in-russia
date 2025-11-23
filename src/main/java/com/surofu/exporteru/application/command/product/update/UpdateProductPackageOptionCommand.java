package com.surofu.exporteru.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Command for updating an existing additional product packaging options")
public record UpdateProductPackageOptionCommand(
    @Schema(description = "Name of the package option",
        example = "Gift wrapping",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    Map<String, String> nameTranslations,

    @Schema(description = "Additional price for this package option",
        example = "9.99",
        requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal price,

    @Schema(description = "Price unit for this package option",
        example = "RUB",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String priceUnit
) {
}
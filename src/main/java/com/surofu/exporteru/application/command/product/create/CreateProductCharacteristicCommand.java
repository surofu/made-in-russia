package com.surofu.exporteru.application.command.product.create;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Command for creating product characteristics")
public record CreateProductCharacteristicCommand(
    @Schema(description = "Name of the characteristic",
        example = "Weight",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    Map<String, String> nameTranslations,

    @Schema(description = "Value of the characteristic",
        example = "1.2 kg",
        requiredMode = Schema.RequiredMode.REQUIRED)
    String value,

    Map<String, String> valueTranslations
) {
}

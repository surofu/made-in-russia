package com.surofu.exporteru.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Command for updating an existing product characteristics")
public record UpdateProductCharacteristicCommand(
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

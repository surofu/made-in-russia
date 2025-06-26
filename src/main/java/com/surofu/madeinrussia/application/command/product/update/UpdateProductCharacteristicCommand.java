package com.surofu.madeinrussia.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command for updating an existing product characteristics")
public record UpdateProductCharacteristicCommand(
        @Schema(description = "Name of the characteristic",
                example = "Weight",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Value of the characteristic",
                example = "1.2 kg",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String value
) {
}

package com.surofu.madeinrussia.application.command.product.update;

import com.surofu.madeinrussia.application.dto.TranslationDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command for updating an existing product characteristics")
public record UpdateProductCharacteristicCommand(
        @Schema(description = "Name of the characteristic",
                example = "Weight",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        TranslationDto nameTranslations,

        @Schema(description = "Value of the characteristic",
                example = "1.2 kg",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String value,

        TranslationDto valueTranslations
) {
}

package com.surofu.exporteru.application.command.product.create;

import com.surofu.exporteru.application.dto.translation.TranslationDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command for creating product characteristics")
public record CreateProductCharacteristicCommand(
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

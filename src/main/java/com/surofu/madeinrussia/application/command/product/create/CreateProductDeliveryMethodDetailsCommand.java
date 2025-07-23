package com.surofu.madeinrussia.application.command.product.create;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command for creating product delivery method details")
public record CreateProductDeliveryMethodDetailsCommand(
        @Schema(description = "Name of the delivery method detail",
                example = "Delivery Time",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        TranslationDto nameTranslations,

        @Schema(description = "Value of the delivery method detail",
                example = "2-3 business days",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String value,

        TranslationDto valueTranslations
) {
}

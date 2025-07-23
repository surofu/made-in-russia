package com.surofu.madeinrussia.application.command.product.create;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command for creating frequently asked questions about a product")
public record CreateProductFaqCommand(
        @Schema(description = "FAQ question text",
                example = "What's included in the box?",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String question,

        TranslationDto questionTranslations,

        @Schema(description = "Answer to the FAQ question",
                example = "The box includes the device, charging cable, and documentation",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String answer,

        TranslationDto answerTranslations
) {
}
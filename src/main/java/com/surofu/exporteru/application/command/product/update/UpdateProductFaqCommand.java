package com.surofu.exporteru.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Command for updating an existing frequently asked questions about a product")
public record UpdateProductFaqCommand(
        @Schema(description = "FAQ question text",
                example = "What's included in the box?",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String question,

        Map<String, String> questionTranslations,

        @Schema(description = "Answer to the FAQ question",
                example = "The box includes the device, charging cable, and documentation",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String answer,

        Map<String, String> answerTranslations
) {
}
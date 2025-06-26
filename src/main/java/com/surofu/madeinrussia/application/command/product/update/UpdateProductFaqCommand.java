package com.surofu.madeinrussia.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command for updating an existing frequently asked questions about a product")
public record UpdateProductFaqCommand(
        @Schema(description = "FAQ question text",
                example = "What's included in the box?",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String question,

        @Schema(description = "Answer to the FAQ question",
                example = "The box includes the device, charging cable, and documentation",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String answer
) {
}
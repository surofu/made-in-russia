package com.surofu.madeinrussia.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Command for updating an existing product vendor details, including descriptions and alternative texts for media")
public record UpdateProductVendorDetailsCommand(
        @Schema(
                description = "Main product description (short summary of key features)",
                example = "High-quality stainless steel kitchenware",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String mainDescription,

        @Schema(
                description = "Additional detailed description (features, specifications, usage instructions, etc.)",
                example = "Includes 10-piece set with non-slip handles. Dishwasher safe.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String furtherDescription,

        @Schema(
                description = "Alternative texts for product media (images, videos) for accessibility (SEO and screen readers)",
                example = "[\"Stainless steel frying pan on a stove\", \"10-piece kitchen set packaging\"]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        List<String> mediaAltTexts
) {
}
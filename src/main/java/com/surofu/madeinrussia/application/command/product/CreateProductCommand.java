package com.surofu.madeinrussia.application.command.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Command for creating a new product")
public record CreateProductCommand(
        @Schema(description = "Product title", example = "iPhone 15 Pro Max", required = true)
        String title,

        @Schema(description = "Main product description", example = "Latest iPhone with advanced camera system", required = true)
        String mainDescription,

        @Schema(description = "Further detailed product description", example = "Extended description with technical details", required = true)
        String furtherDescription,

        @Schema(description = "Primary product description", example = "Primary marketing description", required = true)
        String primaryDescription,

        @Schema(description = "Category ID", example = "1", required = true)
        Long categoryId,

        @Schema(description = "List of delivery method IDs", example = "[1, 2, 3]", required = true)
        List<Long> deliveryMethodIds,

        @Schema(description = "List of product prices", required = true)
        List<CreateProductPriceCommand> prices,

        @Schema(description = "List of product characteristics", required = true)
        List<CreateProductCharacteristicCommand> characteristics,

        @Schema(description = "List of frequently asked questions", required = true)
        List<CreateProductFaqCommand> faq
) {
}

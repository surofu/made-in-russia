package com.surofu.exporteru.application.command.product.create;

import com.surofu.exporteru.application.dto.translation.TranslationDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Command for creating a new product")
public record CreateProductCommand(
        @Schema(description = "Product title", example = "iPhone 15 Pro Max", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        TranslationDto titleTranslations,

        @Schema(description = "Main product description", example = "Latest iPhone with advanced camera system", requiredMode = Schema.RequiredMode.REQUIRED)
        String mainDescription,

        TranslationDto mainDescriptionTranslations,

        @Schema(description = "Further detailed product description", example = "Extended description with technical details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String furtherDescription,

        TranslationDto furtherDescriptionTranslations,

        @Schema(description = "Category ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long categoryId,

        @Schema(description = "List of delivery method IDs", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
        List<Long> deliveryMethodIds,

        @Schema(description = "List of similar product IDs (for recommendations)",
                example = "[101, 205, 310]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<Long> similarProducts,

        @Schema(description = "List of product prices", requiredMode = Schema.RequiredMode.REQUIRED)
        List<CreateProductPriceCommand> prices,

        @Schema(description = "List of product characteristics", requiredMode = Schema.RequiredMode.REQUIRED)
        List<CreateProductCharacteristicCommand> characteristics,

        @Schema(description = "List of frequently asked questions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<CreateProductFaqCommand> faq,

        @Schema(description = "List of frequently asked questions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<CreateProductDeliveryMethodDetailsCommand> deliveryMethodDetails,

        @Schema(description = "List of product package options", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<CreateProductPackageOptionCommand> packageOptions,

        @Schema(description = "Minimum order quantity required",
                example = "5",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer minimumOrderQuantity,

        @Schema(description = "Expiration date/time for the discount (ISO 8601 format)",
                example = "2025-12-31T23:59:59Z",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Integer discountExpirationDate,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<CreateProductMediaAltTextCommand> mediaAltTexts
) {
}

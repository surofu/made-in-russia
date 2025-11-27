package com.surofu.exporteru.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;

@Schema(description = "Command for updating an existing product")
public record UpdateProductCommand(
    @Schema(description = "Product title", example = "iPhone 15 Pro Max", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String title,

    Map<String, String> titleTranslations,

    @Schema(description = "Main product description", example = "Latest iPhone with advanced camera system", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String mainDescription,

    Map<String, String> mainDescriptionTranslations,

    @Schema(description = "Further detailed product description", example = "Extended description with technical details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String furtherDescription,

    Map<String, String> furtherDescriptionTranslations,

    @Validated
    @NotNull(message = "Категория товара не может быть пустой")
    @Schema(description = "Category ID", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Long categoryId,

    @Schema(description = "List of delivery method IDs", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<Long> deliveryMethodIds,

    @Schema(description = "List of delivery term IDs", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<Long> deliveryTermIds,

    @Schema(description = "List of similar product IDs (for recommendations)",
        example = "[101, 205, 310]",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<Long> similarProducts,

    @Schema(description = "List of product prices", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<UpdateProductPriceCommand> prices,

    @Schema(description = "List of product characteristics", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<UpdateProductCharacteristicCommand> characteristics,

    @Schema(description = "List of frequently asked questions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<UpdateProductFaqCommand> faq,

    @Schema(description = "List of frequently asked questions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<UpdateProductDeliveryMethodDetailsCommand> deliveryMethodDetails,

    @Schema(description = "List of product package options", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<UpdateProductPackageOptionCommand> packageOptions,

    @Schema(description = "Minimum order quantity required",
        example = "5",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer minimumOrderQuantity,

    @Schema(description = "Expiration date/time for the discount (ISO 8601 format)",
        example = "2025-12-31T23:59:59Z",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer discountExpirationDate,

    List<UpdateOldMediaDto> oldProductMedia,

    List<UpdateOldMediaDto> oldAboutVendorMedia,

    List<UpdateProductMediaAltTextCommand> mediaAltTexts
) {
}

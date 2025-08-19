package com.surofu.madeinrussia.application.command.product.update;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Command for updating an existing product pricing with quantity tiers and discounts")
public record UpdateProductPriceCommand(
        @Schema(description = "Minimum quantity for this price tier",
                example = "1-2",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String quantityFrom,

        @Schema(description = "Maximum quantity for this price tier (null for unlimited)",
                example = "10",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String quantityTo,

        @Schema(description = "Currency code (RUB, USD, CNY)",
                example = "USD",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String currency,

        @Schema(description = "Unit of measurement for pricing",
                example = "piece",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String unit,

        @Schema(description = "Base price per unit",
                example = "99.99",
                requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal price,

        @Schema(description = "Discount amount (absolute or percentage)",
                example = "10.00",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        BigDecimal discount
) {
}

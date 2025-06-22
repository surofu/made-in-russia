package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productPrice.ProductPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ProductPrice",
        description = "Represents pricing information for a product including discounts and quantity ranges",
        example = """
                {
                  "id": 123,
                  "from": 1.0,
                  "to": 10.0,
                  "currency": "USD",
                  "unit": "kg",
                  "originalPrice": 99.99,
                  "discount": 15.00,
                  "discountedPrice": 84.99,
                  "minimumOrderQuantity": 5,
                  "discountExpirationDate": "2025-12-31T23:59:59Z",
                  "creationDate": "2025-05-01T10:00:00Z",
                  "lastModificationDate": "2025-05-15T14:30:00Z"
                }
                """
)
public final class ProductPriceDto implements Serializable {

    @Schema(
            description = "Unique identifier of the price entry",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Minimum quantity for this price tier (inclusive)",
            example = "2",
            type = "integer",
            minimum = "0",
            defaultValue = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer from;

    @Schema(
            description = "Maximum quantity for this price tier (exclusive). Null indicates no upper limit.",
            example = "5",
            type = "integer",
            minimum = "0",
            defaultValue = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer to;

    @Schema(
            description = "Currency code in ISO 4217 format",
            example = "USD",
            pattern = "^[A-Z]{3}$",
            maxLength = 3,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String currency;

    @Schema(
            description = "Unit of measurement for the product",
            example = "kg",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String unit;

    @Schema(
            description = "Original price before any discounts",
            example = "99.99",
            type = "number",
            format = "decimal",
            minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal originalPrice;

    @Schema(
            description = "Discount percentage (0-100)",
            example = "15.00",
            type = "number",
            format = "decimal",
            minimum = "0",
            maximum = "100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal discount;

    @Schema(
            description = "Final price after applying discounts",
            example = "84.99",
            type = "number",
            format = "decimal",
            minimum = "0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private BigDecimal discountedPrice;

    @Schema(
            description = "Minimum quantity that must be ordered",
            example = "5",
            type = "integer",
            minimum = "1",
            defaultValue = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer minimumOrderQuantity;

    @Schema(
            description = "Expiration date/time for the discount (if applicable)",
            example = "2025-12-31T23:59:59Z",
            type = "string",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ZonedDateTime expiryDate;

    @Schema(
            description = "Timestamp when the price was created",
            example = "2025-05-01T10:00:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the price was last modified",
            example = "2025-05-15T14:30:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductPriceDto of(ProductPrice productPrice) {
        return ProductPriceDto.builder()
                .id(productPrice.getId())
                .from(productPrice.getQuantityRange().getFrom())
                .to(productPrice.getQuantityRange().getTo())
                .currency(productPrice.getCurrency().toString())
                .unit(productPrice.getUnit().toString())
                .originalPrice(productPrice.getOriginalPrice().getValue())
                .discount(productPrice.getDiscount().getValue())
                .discountedPrice(productPrice.getDiscountedPrice().getValue())
                .minimumOrderQuantity(productPrice.getMinimumOrderQuantity().getValue())
                .expiryDate(productPrice.getExpirationDate().getValue())
                .creationDate(productPrice.getCreationDate().getValue())
                .lastModificationDate(productPrice.getLastModificationDate().getValue())
                .build();
    }
}
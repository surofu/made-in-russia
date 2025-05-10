package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.Product;
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
        name = "Product",
        description = "Represents a product DTO"
)
public final class ProductDto implements Serializable {

    @Schema(
            description = "Unique identifier of the product",
            example = "12345"
    )
    private Long id;

    @Schema(
            description = "Delivery method associated with this product",
            implementation = DeliveryMethodDto.class
    )
    private DeliveryMethodDto deliveryMethod;

    @Schema(
            description = "Category this product belongs to",
            implementation = CategoryDto.class
    )
    private CategoryDto category;

    @Schema(
            description = "Title/name of the product",
            example = "Premium Wireless Headphones",
            maxLength = 255
    )
    private String title;

    @Schema(
            description = "Base price of the product before discounts",
            example = "199.99",
            type = "number",
            format = "decimal"
    )
    private BigDecimal price;

    @Schema(
            description = "Current discount percentage applied (0-100)",
            example = "15.00",
            minimum = "0",
            maximum = "100",
            type = "number",
            format = "decimal"
    )
    private BigDecimal discount;

    @Schema(
            description = "Final price after applying discounts",
            example = "169.99",
            type = "number",
            format = "decimal"
    )
    private BigDecimal discountedPrice;

    @Schema(
            description = "URL of the product's main image",
            example = "https://example.com/images/headphones.jpg",
            format = "uri"
    )
    private String imageUrl;

    @Schema(
            description = "Timestamp when the product was created",
            example = "2023-05-15T09:30:00",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the product was last modified",
            example = "2023-06-20T14:15:30",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductDto of(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .deliveryMethod(DeliveryMethodDto.of(product.getDeliveryMethod()))
                .category(CategoryDto.of(product.getCategory()))
                .title(product.getTitle().getTitle())
                .price(product.getPrice().getPrice())
                .discount(product.getPrice().getDiscount())
                .discountedPrice(product.getPrice().getDiscountedPrice())
                .imageUrl(product.getImageUrl().getImageUrl())
                .creationDate(product.getCreationDate())
                .lastModificationDate(product.getLastModificationDate())
                .build();
    }
}
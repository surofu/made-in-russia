package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ProductSummary",
        description = "Compact representation of product information for listings and summary views",
        example = """
                {
                  "id": 42,
                  "category": {
                      "id": 5,
                      "name": "Smartphones & Accessories",
                      "slug": "l1_rastenievodstvo-i-zhivotnovodstvo",
                      "imageUrl": "https://images.unsplash.com/photo-1515446134809-993c501ca304?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                      "children": [
                            {
                              "id": 1,
                              "name": "Wood",
                              "slug": "l2_wood",
                              "imageUrl": null,
                              "children": [],
                              "creationDate": "2025-05-04T09:17:20.767615Z",
                              "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                            },
                            {
                              "id": 2,
                              "name": "Stone",
                              "slug": "l2_stone",
                              "imageUrl": null,
                              "children": [],
                              "creationDate": "2025-05-04T09:17:20.767615Z",
                              "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                            }
                          ],
                      "childrenCount": 2,
                      "creationDate": "2025-05-04T09:17:20.767615Z",
                      "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                  },
                  "deliveryMethods": [
                    {
                      "id": 1,
                      "name": "Express Delivery",
                      "creationDate": "2025-05-04T09:17:20.767615Z",
                      "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                    },
                    {
                      "id": 2,
                      "name": "Standard Delivery",
                      "creationDate": "2025-05-04T09:17:20.767615Z",
                      "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                    }
                  ],
                  "title": "Premium Wireless Headphones",
                  "originalPrice": 199.99,
                  "discount": 15.00,
                  "discountedPrice": 169.99,
                  "priceCurrency": "USD",
                  "rating": 4.5,
                  "previewImageUrl": "https://example.com/images/headphones-preview.jpg",
                  "creationDate": "2025-05-04T09:17:20.767615Z",
                  "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                }
                """
)
public final class ProductSummaryViewDto implements Serializable {

    @Schema(
            description = "Unique identifier of the product",
            example = "12345",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Publisher of the product",
            implementation = UserDto.class,
            example = """
                    {
                      "id": 12345,
                      "role": "User",
                      "email": "user@example.com",
                      "login": "john_doe",
                      "phoneNumber": "+79123456789",
                      "region": "Moscow, Russia",
                      "registrationDate": "2025-05-04T09:17:20.767615Z",
                      "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                    }
                    """,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UserDto user;

    @Schema(
            description = "Category this product belongs to",
            implementation = CategoryDto.class,
            example = """
                    {
                      "id": 5,
                      "name": "Electronics",
                      "creationDate": "2023-01-10T10:00:00Z",
                      "lastModificationDate": "2023-06-15T14:30:00Z"
                    }
                    """
    )
    private CategoryDto category;

    @Schema(
            description = "Available delivery methods for this product",
            type = "array",
            implementation = DeliveryMethodDto[].class,
            example = """
                    [
                      {
                        "id": 1,
                        "name": "Express Delivery",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      },
                      {
                        "id": 2,
                        "name": "Standard Delivery",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      }
                    ]
                    """
    )
    private List<DeliveryMethodDto> deliveryMethods;

    @Schema(
            description = "Title/name of the product",
            example = "Premium Wireless Headphones with Noise Cancellation",
            minLength = 3,
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;

    @Schema(
            description = "Original price of the product before discounts",
            example = "199.99",
            type = "number",
            format = "decimal",
            minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal originalPrice;

    @Schema(
            description = "Current discount percentage applied (0-100)",
            example = "15.00",
            minimum = "0",
            maximum = "100",
            type = "number",
            format = "decimal",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal discount;

    @Schema(
            description = "Final price after applying discounts",
            example = "169.99",
            type = "number",
            format = "decimal",
            minimum = "0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private BigDecimal discountedPrice;

    @Schema(
            description = "Price currency",
            example = "USD",
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String priceCurrency;

    @Schema(
            description = """
                    Product's average rating based on customer reviews.
                    Calculated as arithmetic mean of all review ratings.
                    Minimum possible value: 1.0 (all 1-star reviews)
                    Maximum possible value: 5.0 (all 5-star reviews)
                    Returns null if product has no reviews.
                    Automatically updated when new reviews are added.""",
            example = "4.2",
            type = "number",
            format = "double",
            minimum = "1.0",
            maximum = "5.0",
            multipleOf = 0.1,
            nullable = true,
            accessMode = Schema.AccessMode.READ_ONLY,
            extensions = {
                    @Extension(
                            name = "x-validation",
                            properties = {
                                    @ExtensionProperty(name = "minRating", value = "1"),
                                    @ExtensionProperty(name = "maxRating", value = "5"),
                                    @ExtensionProperty(name = "rounding", value = "nearest 0.1")
                            }
                    )
            }
    )
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    @Digits(integer = 1, fraction = 1)
    private Double rating;

    @Schema(
            description = "URL of the product's preview image",
            example = "https://media.tenor.com/x8v1oNUOmg4AAAAM/rickroll-roll.gif",
            format = "uri",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String previewImageUrl;

    @Schema(
            description = "Timestamp when the product was created",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the product was last modified",
            example = "2025-05-10T11:30:45.123456Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    public static ProductSummaryViewDto of(ProductSummaryView productSummaryView, String lang) {
        return ProductSummaryViewDto.builder()
                .id(productSummaryView.getId())
                .user(productSummaryView.getUser())
                .category(productSummaryView.getCategory())
                .deliveryMethods(productSummaryView.getDeliveryMethods())
                .title(productSummaryView.getTitleByLang(lang))
                .originalPrice(productSummaryView.getOriginPrice() == null ? null : productSummaryView.getOriginPrice().setScale(2, RoundingMode.DOWN))
                .discount(productSummaryView.getDiscount())
                .discountedPrice(productSummaryView.getDiscountedPrice() == null ? null : productSummaryView.getDiscountedPrice().setScale(2, RoundingMode.DOWN))
                .priceCurrency(productSummaryView.getPriceCurrencyCode() == null ? null : productSummaryView.getPriceCurrencyCode().toString())
                .rating(productSummaryView.getRating())
                .previewImageUrl(productSummaryView.getPreviewImageUrl())
                .creationDate(productSummaryView.getCreationDate())
                .lastModificationDate(productSummaryView.getLastModificationDate())
                .build();
    }
}
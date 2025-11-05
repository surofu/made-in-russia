package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.dto.*;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.translation.TranslationDto;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.application.utils.HstoreParser;
import com.surofu.exporteru.infrastructure.persistence.product.ProductWithTranslationsView;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Product with translations",
        description = "Represents a product DTO with all text localizations"
)
public class ProductWithTranslationsDto implements Serializable {

    @Schema(
            description = "Unique identifier of the product",
            example = "42",
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
    private VendorDto user;

    @Schema(
            description = "Category this product belongs to",
            implementation = CategoryDto.class,
            example = """
                    {
                      "id": 5,
                      "name": "Electronics",
                      "creationDate": "2025-05-04T09:17:20.767615Z",
                      "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                    }
                    """
    )
    private CategoryDto category;

    @Schema(
            description = "Delivery method list associated with this product",
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
            description = "List of product media (images, videos)",
            type = "array",
            implementation = ProductMediaDto[].class,
            example = """
                    [
                      {
                        "id": 1,
                        "mediaType": "image",
                        "mimeType": "image/jpeg",
                        "url": "https://cdn.example.com/products/123/image1.jpg",
                        "altText": "Front view of the product",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      },
                      {
                        "id": 2,
                        "mediaType": "video",
                        "mimeType": "video/mp4",
                        "url": "https://cdn.example.com/products/123/video.mp4",
                        "altText": "Product demonstration video",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      }
                    ]
                    """
    )
    private List<ProductMediaWithTranslationsDto> media;

    private List<SimilarProductDto> similarProducts;

    @Schema(
            description = "Technical specifications and product characteristics",
            type = "array",
            implementation = ProductCharacteristicWithTranslationsDto[].class
    )
    private List<ProductCharacteristicWithTranslationsDto> characteristics;

    @Schema(
            description = "Represents a frequently asked question and answer for a product",
            type = "array",
            implementation = ProductFaqWithTranslationDto[].class
    )
    private List<ProductFaqWithTranslationDto> faq;

    @Schema(
            description = "Represents pricing information for a product including discounts and quantity ranges",
            type = "array",
            implementation = ProductPriceDto[].class,
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
    private List<ProductPriceDto> prices;

    @Schema(
            description = "Unique Article of the product",
            example = "drJo-3286",
            minLength = 9,
            maxLength = 9,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String article;

    @Schema(
            description = "Title/name of the product",
            example = "Premium Wireless Headphones",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;

    TranslationDto titleTranslations;

    @Schema(
            description = "Full product description with features, specifications and usage details. HTML supported.",
            example = "<p>Flagship smartphone with 6.7\" AMOLED 120Hz display, Snapdragon 8 Gen 2 and 108MP triple camera.</p>",
            maxLength = 20000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mainDescription;

    TranslationDto mainDescriptionTranslations;

    @Schema(
            description = "Additional technical specifications and compatibility details",
            example = "- 5G/Wi-Fi 6 support\n- IP68 water resistance\n- Dolby Atmos stereo speakers",
            maxLength = 5000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String furtherDescription;

    TranslationDto furtherDescriptionTranslations;

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

    private Integer reviewsCount;

    @Schema(
            description = "URL of the product's preview image",
            example = "https://media.tenor.com/x8v1oNUOmg4AAAAM/rickroll-roll.gif",
            format = "uri",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String previewImageUrl;

    private List<ProductReviewMediaDto> reviewsMedia;

    private List<ProductDeliveryMethodDetailsWithTranslationsDto> deliveryMethodsDetails;

    private List<ProductPackageOptionWithTranslationsDto> packagingOptions;

    @Schema(
            description = "Minimum quantity that must be ordered",
            example = "1-5",
            type = "integer",
            minimum = "1",
            defaultValue = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String minimumOrderQuantity;

    @Schema(
            description = "Expiration date/time for the discount (if applicable)",
            example = "2025-12-31T23:59:59Z",
            type = "string",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long daysBeforeDiscountExpires;

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
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductWithTranslationsDto of(ProductWithTranslationsView view) {
        return ProductWithTranslationsDto.builder()
                .id(view.getId())
                .article(view.getArticleCode())
                .title(view.getTitle())
                .titleTranslations(TranslationDto.of(HstoreParser.fromString(view.getTitleTranslations())))
                .mainDescription(view.getMainDescription())
                .mainDescriptionTranslations(TranslationDto.of(HstoreParser.fromString(view.getMainDescriptionTranslations())))
                .furtherDescription(view.getFurtherDescription())
                .furtherDescriptionTranslations(TranslationDto.of(HstoreParser.fromString(view.getFurtherDescriptionTranslations())))
                .rating(view.getRating())
                .reviewsCount(view.getReviewsCount())
                .previewImageUrl(view.getPreviewImageUrl())
                .minimumOrderQuantity(view.getMinimumOrderQuantity() == null ? null : view.getMinimumOrderQuantity().toString())
                .daysBeforeDiscountExpires(view.getDiscountExpirationDate() == null ? null : getDaysBeforeDiscountExpires(view.getDiscountExpirationDate().atZone(ZoneId.systemDefault())))
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }

    private static Long getDaysBeforeDiscountExpires(ZonedDateTime discountExpirationDate) {
        if (discountExpirationDate == null) {
            return null;
        }

        return LocalDate.now().until(discountExpirationDate.toLocalDate(), ChronoUnit.DAYS);
    }
}
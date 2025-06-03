package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.application.dto.temp.TempProductDeliveryMethodDetails;
import com.surofu.madeinrussia.application.dto.temp.TempProductPackagingOptionDetails;
import com.surofu.madeinrussia.application.dto.temp.TempVendorDetails;
import com.surofu.madeinrussia.core.model.media.MediaType;
import com.surofu.madeinrussia.core.model.product.Product;
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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Product",
        description = "Represents a product DTO"
)
public class ProductDto implements Serializable {

    @Schema(
            description = "Unique identifier of the product",
            example = "42",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

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
    private List<ProductMediaDto> media;

    @Schema(
            description = "Technical specifications and product characteristics",
            type = "array",
            implementation = ProductCharacteristicDto[].class,
            example = """
                    [
                      {
                        "id": 101,
                        "name": "Weight",
                        "value": "1.2 kg",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      },
                      {
                        "id": 102,
                        "name": "Dimensions",
                        "value": "30 x 20 x 5 cm",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      },
                      {
                        "id": 103,
                        "name": "Material",
                        "value": "Aluminum alloy",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      },
                      {
                        "id": 104,
                        "name": "Warranty",
                        "value": "2 years",
                        "creationDate": "2025-05-04T09:17:20.767615Z",
                        "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                      }
                    ]
                    """
    )
    private List<ProductCharacteristicDto> characteristics;

    @Schema(
            description = "Represents a frequently asked question and answer for a product",
            type = "array",
            implementation = ProductFaqDto[].class,
            example = """
                    {
                      "id": 42,
                      "question": "What is the warranty period for this product?",
                      "answer": "This product comes with a 2-year manufacturer warranty.",
                      "creationDate": "2025-05-15T10:30:00Z",
                      "lastModificationDate": "2025-06-20T14:15:00Z"
                    }
                    """
    )
    private List<ProductFaqDto> faq;

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

    @Schema(
            description = "Full product description with features, specifications and usage details. HTML supported.",
            example = "<p>Flagship smartphone with 6.7\" AMOLED 120Hz display, Snapdragon 8 Gen 2 and 108MP triple camera.</p>",
            maxLength = 20000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mainDescription;

    @Schema(
            description = "Additional technical specifications and compatibility details",
            example = "- 5G/Wi-Fi 6 support\n- IP68 water resistance\n- Dolby Atmos stereo speakers",
            maxLength = 5000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String furtherDescription;

    @Schema(
            description = "Short product summary for cards and previews (1-2 sentences)",
            example = "2023 flagship smartphone with best-in-class camera and performance",
            maxLength = 5000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String summaryDescription;

    @Schema(
            description = "Primary short product description for cards",
            example = "2023 flagship smartphone with best-in-class camera and performance",
            maxLength = 5000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String primaryDescription;

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
            example = "https://example.com/images/headphones.jpg",
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
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    // TODO: !!! Заглушки

    private Integer ordersCount = new Random().nextInt(100);

    private List<ProductReviewMediaDto> reviewsMedia = List.of(
            new ProductReviewMediaDto(1L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(2L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(3L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(4L, MediaType.VIDEO.getName(), "video/mp4", "https://v.ftcdn.net/01/62/37/70/700_F_162377062_Y15dX6CakFKDesem5fucH18pcnjNhng5_ST.mp4", "Video Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(5L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(6L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(7L, MediaType.VIDEO.getName(), "video/mp4", "https://v.ftcdn.net/01/62/37/70/700_F_162377062_Y15dX6CakFKDesem5fucH18pcnjNhng5_ST.mp4", "Video Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(8L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(9L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
            new ProductReviewMediaDto(10L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now())
    );

    private TempVendorDetails aboutVendor;

    private List<TempProductDeliveryMethodDetails> deliveryMethodsDetails;

    private List<TempProductPackagingOptionDetails> packagingOptions;

    @Schema(hidden = true)
    public static ProductDto of(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .category(CategoryDto.of(product.getCategory()))
                .deliveryMethods(product.getDeliveryMethods().stream()
                        .map(DeliveryMethodDto::of)
                        .collect(Collectors.toList())
                )
                .media(product.getMedia().stream().map(ProductMediaDto::of).toList())
                .characteristics(product.getCharacteristics().stream().map(ProductCharacteristicDto::of).toList())
                .prices(product.getPrices().stream().map(ProductPriceDto::of).toList())
                .article(product.getArticleCode().toString())
                .title(product.getTitle().getValue())
                .mainDescription(product.getDescription().getMainDescription())
                .furtherDescription(product.getDescription().getFurtherDescription())
                .summaryDescription(product.getDescription().getSummaryDescription())
                .primaryDescription(product.getDescription().getPrimaryDescription())
                .previewImageUrl(product.getPreviewImageUrl().getValue())
                .creationDate(product.getCreationDate().getValue())
                .lastModificationDate(product.getLastModificationDate().getValue())
                .rating(product.getRating())
                .reviewsCount(new Random().nextInt(100))
                .ordersCount(new Random().nextInt(100))
                .reviewsMedia(List.of(
                        new ProductReviewMediaDto(1L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(2L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(3L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(4L, MediaType.VIDEO.getName(), "video/mp4", "https://v.ftcdn.net/01/62/37/70/700_F_162377062_Y15dX6CakFKDesem5fucH18pcnjNhng5_ST.mp4", "Video Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(5L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(6L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(7L, MediaType.VIDEO.getName(), "video/mp4", "https://v.ftcdn.net/01/62/37/70/700_F_162377062_Y15dX6CakFKDesem5fucH18pcnjNhng5_ST.mp4", "Video Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(8L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(9L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now()),
                        new ProductReviewMediaDto(10L, MediaType.IMAGE.getName(), "image/png", "https://images.unsplash.com/photo-1531824475211-72594993ce2a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8d29vZHxlbnwwfHwwfHx8MA%3D%3D", "Image Alt Text", ZonedDateTime.now(), ZonedDateTime.now())
                ))
                .faq(product.getFaq().stream().map(ProductFaqDto::of).toList())
                .reviewsCount(product.getReviewsCount())
                .aboutVendor(new TempVendorDetails())
                .deliveryMethodsDetails(List.of(new TempProductDeliveryMethodDetails(), new TempProductDeliveryMethodDetails()))
                .packagingOptions(List.of(new TempProductPackagingOptionDetails(), new TempProductPackagingOptionDetails()))
                .build();
    }
}
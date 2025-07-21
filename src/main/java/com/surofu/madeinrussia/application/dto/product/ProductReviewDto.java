package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ProductReview",
        description = "Represents a product review with user feedback and rating"
)
public final class ProductReviewDto implements Serializable {

    @Schema(
            description = "Unique identifier of the review",
            example = "42",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Author of the review",
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
                    """
    )
    private UserDto author;

    @Schema(
            description = "Review text content",
            example = "This product exceeded my expectations! The quality is excellent and it arrived earlier than expected.",
            minLength = 10,
            maxLength = 2000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String text;

    @Schema(
            description = "Media attachments for the product review (images, videos etc.) sorted by position in descending order",
            type = "array",
            implementation = ProductReviewMediaDto.class,
            example = """
        [
          {
            "id": 1,
            "mediaType": "video",
            "mimeType": "video/mp4",
            "url": "https://example.com/media/reviews/wood_video1.mp4",
            "altText": "Обзор партии красного дерева",
            "creationDate": "2025-05-04T09:17:20.767615Z",
            "lastModificationDate": "2025-05-04T09:17:20.767615Z"
          },
          {
            "id": 2,
            "mediaType": "image",
            "mimeType": "image/jpeg",
            "url": "https://example.com/media/reviews/wood2.jpg",
            "altText": "Текстура красного дерева крупным планом",
            "creationDate": "2025-05-04T09:17:20.767615Z",
            "lastModificationDate": "2025-05-04T09:17:20.767615Z"
          },
          {
            "id": 3,
            "mediaType": "image",
            "mimeType": "image/jpeg",
            "url": "https://example.com/media/reviews/wood1.jpg",
            "altText": "Красное дерево - общий вид партии",
            "creationDate": "2025-05-04T09:17:20.767615Z",
            "lastModificationDate": "2025-05-04T09:17:20.767615Z"
          }
        ]
        """
    )
    private List<ProductReviewMediaDto> media;

    @Schema(
            description = "Numeric rating of the product (1-5 stars)",
            example = "5",
            minimum = "1",
            maximum = "5",
            type = "integer",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer rating;

    @Schema(
            description = "Timestamp when the review was created",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the review was last modified",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    public static ProductReviewDto of(ProductReview productReview) {
        return ProductReviewDto.builder()
                .id(productReview.getId())
                .author(UserDto.of(productReview.getUser()))
                .text(productReview.getContent().toString())
                .media(productReview.getMedia().stream().map(ProductReviewMediaDto::of).toList())
                .rating(productReview.getRating().getValue())
                .creationDate(productReview.getCreationDate().getValue())
                .lastModificationDate(productReview.getLastModificationDate().getValue())
                .build();
    }
}
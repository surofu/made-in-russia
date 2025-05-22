package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Represents a product media content (images, videos)",
        name = "ProductMedia"
)
public final class ProductMediaDto {

    @Schema(
            description = "Unique identifier of the media item",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Type of media content (IMAGE, VIDEO)",
            example = "image",
            allowableValues = {"image", "video"}
    )
    private String mediaType;

    @Schema(
            description = "MIME type of the media content",
            example = "image/jpeg"
    )
    private String mimeType;

    @Schema(
            description = "Absolute URL to access the media content",
            example = "https://cdn.example.com/products/123/image.jpg"
    )
    private String url;

    @Schema(
            description = "Alternative text for accessibility (screen readers) and SEO",
            example = "Red smartphone with black accessories",
            maxLength = 255
    )
    private String altText;

    @Schema(
            description = "Timestamp when the media was first uploaded",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the media was last modified (null if never modified)",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductMediaDto of(ProductMedia productMedia) {
        return ProductMediaDto.builder()
                .id(productMedia.getId())
                .mediaType(productMedia.getMediaType().getName())
                .mimeType(productMedia.getMimeType().toString())
                .url(productMedia.getUrl().toString())
                .altText(productMedia.getAltText().toString())
                .creationDate(productMedia.getCreationDate().getCreationDate())
                .lastModificationDate(productMedia.getLastModificationDate().getLastModificationDate())
                .build();
    }
}

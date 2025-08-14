package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.core.model.product.media.ProductMedia;
import com.surofu.madeinrussia.infrastructure.persistence.product.media.ProductMediaView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Represents a product media content (images, videos)",
        name = "ProductMedia"
)
public final class ProductMediaDto implements Serializable {

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
            example = "https://media.tenor.com/x8v1oNUOmg4AAAAM/rickroll-roll.gif"
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
                .creationDate(productMedia.getCreationDate().getValue())
                .lastModificationDate(productMedia.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static ProductMediaDto of(ProductMediaView view) {
        return ProductMediaDto.builder()
                .id(view.getId())
                .mediaType(view.getMediaType().getName())
                .mimeType(view.getMimeType())
                .url(view.getUrl())
                .altText(view.getAltText())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}

package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.core.model.product.review.media.ProductReviewMedia;
import com.surofu.exporteru.infrastructure.persistence.product.review.media.ProductReviewMediaView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductReviewMedia")
public final class ProductReviewMediaDto implements Serializable {
  private Long id;
  private String mediaType;
  private String mimeType;
  private String url;
  private String altText;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static ProductReviewMediaDto of(ProductReviewMedia productReviewMedia) {
    return ProductReviewMediaDto.builder()
        .id(productReviewMedia.getId())
        .mediaType(productReviewMedia.getMediaType().getName())
        .mimeType(productReviewMedia.getMimeType().toString())
        .url(productReviewMedia.getUrl().toString())
        .altText(productReviewMedia.getAltText().toString())
        .creationDate(productReviewMedia.getCreationDate().getValue())
        .lastModificationDate(productReviewMedia.getLastModificationDate().getValue())
        .build();
  }

  public static ProductReviewMediaDto of(ProductReviewMediaView view) {
    return ProductReviewMediaDto.builder()
        .id(view.getId())
        .mediaType(view.getMediaType().getName())
        .mimeType(view.getMimeType().toString())
        .url(view.getUrl().toString())
        .altText(view.getAltText().toString())
        .creationDate(view.getCreationDate().getValue())
        .lastModificationDate(view.getLastModificationDate().getValue())
        .build();
  }
}

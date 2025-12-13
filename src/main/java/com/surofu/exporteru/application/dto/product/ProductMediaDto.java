package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.infrastructure.persistence.product.media.ProductMediaView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductMedia")
public final class ProductMediaDto implements Serializable {
  private Long id;
  private String mediaType;
  private String mimeType;
  private String url;
  private String altText;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static ProductMediaDto of(ProductMedia productMedia) {
    return ProductMediaDto.builder()
        .id(productMedia.getId())
        .mediaType(productMedia.getMediaType().getName())
        .mimeType(productMedia.getMimeType().toString())
        .url(productMedia.getUrl().toString())
        .altText(productMedia.getAltText().getLocalizedValue())
        .creationDate(productMedia.getCreationDate().getValue())
        .lastModificationDate(productMedia.getLastModificationDate().getValue())
        .build();
  }

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

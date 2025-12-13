package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.infrastructure.persistence.product.media.ProductMediaWithTranslationsView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProductMedia with translations")
public final class ProductMediaWithTranslationsDto {
  private Long id;
  private String mediaType;
  private String mimeType;
  private String url;
  private String altText;
  private Map<String, String> altTextTranslations;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static ProductMediaWithTranslationsDto of(ProductMediaWithTranslationsView view) {
    return ProductMediaWithTranslationsDto.builder()
        .id(view.getId())
        .mediaType(view.getMediaType().getName())
        .mimeType(view.getMimeType())
        .url(view.getUrl())
        .altText(view.getAltText())
        .altTextTranslations(view.getAltTextTranslationsMap())
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }
}

package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.infrastructure.persistence.product.characteristic.ProductCharacteristicWithTranslationsView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
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
@Schema(name = "ProductCharacteristic with translations")
public final class ProductCharacteristicWithTranslationsDto implements Serializable {
  private Long id;
  private String name;
  private String value;
  private Map<String, String> nameTranslations;
  private Map<String, String> valueTranslations;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  @Schema(hidden = true)
  public static ProductCharacteristicWithTranslationsDto of(
      ProductCharacteristicWithTranslationsView view) {
    return ProductCharacteristicWithTranslationsDto.builder()
        .id(view.getId())
        .name(view.getName())
        .nameTranslations(view.getNameTranslationsMap())
        .value(view.getValue())
        .valueTranslations(view.getValueTranslationsMap())
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }
}
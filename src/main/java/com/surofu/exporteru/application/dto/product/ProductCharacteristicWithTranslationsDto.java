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
@Schema(
    name = "ProductCharacteristic with translations",
    description = "Dto for product characteristics with localization fields"
)
public final class ProductCharacteristicWithTranslationsDto implements Serializable {

  Map<String, String> nameTranslations;
  Map<String, String> valueTranslations;
  @Schema(
      description = "Unique identifier of the characteristic",
      example = "105",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  private Long id;
  @Schema(
      description = "Name of the characteristic (e.g., 'Weight', 'Color')",
      example = "Battery Capacity",
      maxLength = 100,
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  private String name;
  @Schema(
      description = "Value of the characteristic with appropriate units",
      example = "5000 mAh",
      maxLength = 255,
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  private String value;
  @Schema(
      description = "Timestamp when the characteristic was created",
      example = "2025-05-04T09:17:20.767615Z",
      type = "string",
      format = "date-time"
  )
  private ZonedDateTime creationDate;

  @Schema(
      description = "Timestamp when the characteristic was last modified",
      example = "2025-05-04T09:17:20.767615Z",
      type = "string",
      format = "date-time",
      nullable = true
  )
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
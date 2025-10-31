package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.dto.translation.TranslationDto;
import com.surofu.exporteru.application.utils.HstoreParser;
import com.surofu.exporteru.infrastructure.persistence.product.characteristic.ProductCharacteristicWithTranslationsView;
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
        name = "ProductCharacteristic with translations",
        description = "Dto for product characteristics with localization fields"
)
public final class ProductCharacteristicWithTranslationsDto implements Serializable {

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

    TranslationDto nameTranslations;

    @Schema(
            description = "Value of the characteristic with appropriate units",
            example = "5000 mAh",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String value;

    TranslationDto valueTranslations;

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
    public static ProductCharacteristicWithTranslationsDto of(ProductCharacteristicWithTranslationsView view) {
        return ProductCharacteristicWithTranslationsDto.builder()
                .id(view.getId())
                .name(view.getName())
                .nameTranslations(TranslationDto.of(HstoreParser.fromString(view.getNameTranslations())))
                .value(view.getValue())
                .valueTranslations(TranslationDto.of(HstoreParser.fromString(view.getValueTranslations())))
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
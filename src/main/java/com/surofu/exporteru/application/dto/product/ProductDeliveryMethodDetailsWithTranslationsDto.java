package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.application.dto.translation.TranslationDto;
import com.surofu.exporteru.application.utils.HstoreParser;
import com.surofu.exporteru.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsWithTranslationsView;
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
        name = "ProductDeliveryMethodDetails with translations",
        description = "Dto for product delivery method details with localization fields"
)
public final class ProductDeliveryMethodDetailsWithTranslationsDto implements Serializable {

    private Long id;

    private String name;

    private TranslationDto nameTranslations;

    private String value;

    private TranslationDto valueTranslations;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductDeliveryMethodDetailsWithTranslationsDto of(ProductDeliveryMethodDetailsWithTranslationsView view) {
        return ProductDeliveryMethodDetailsWithTranslationsDto.builder()
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

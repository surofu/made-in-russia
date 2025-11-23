package com.surofu.exporteru.application.dto.product;

import com.surofu.exporteru.infrastructure.persistence.product.deliveryMethodDetails.ProductDeliveryMethodDetailsWithTranslationsView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
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

    private Map<String, String> nameTranslations;

    private String value;

    private Map<String, String> valueTranslations;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductDeliveryMethodDetailsWithTranslationsDto of(ProductDeliveryMethodDetailsWithTranslationsView view) {
        return ProductDeliveryMethodDetailsWithTranslationsDto.builder()
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

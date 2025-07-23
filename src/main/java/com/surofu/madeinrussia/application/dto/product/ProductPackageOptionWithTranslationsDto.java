package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import com.surofu.madeinrussia.infrastructure.persistence.product.packageOption.ProductPackageOptionWithTranslationsView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ProductPackageOptionWithTranslationsDto implements Serializable {

    private Long id;

    private String name;

    private TranslationDto nameTranslations;

    private BigDecimal price;

    private String priceUnit;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductPackageOptionWithTranslationsDto of(ProductPackageOptionWithTranslationsView view) {
        return ProductPackageOptionWithTranslationsDto.builder()
                .id(view.getId())
                .name(view.getName())
                .nameTranslations(TranslationDto.of(HstoreParser.fromString(view.getNameTranslations())))
                .price(view.getPrice())
                .priceUnit(view.getPriceUnit())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}

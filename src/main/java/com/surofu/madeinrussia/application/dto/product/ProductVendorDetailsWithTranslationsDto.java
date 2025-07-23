package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.ProductVendorDetailsWithTranslationsView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ProductVendorDetailsWithTranslationsDto implements Serializable {

    private Long id;

    private List<ProductVendorDetailsMediaDto> media = new ArrayList<>();

    private String mainDescription;

    private TranslationDto mainDescriptionTranslations;

    private String furtherDescription;

    private TranslationDto furtherDescriptionTranslations;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductVendorDetailsWithTranslationsDto of(ProductVendorDetailsWithTranslationsView view) {
        if (view == null) {
            return null;
        }

        return ProductVendorDetailsWithTranslationsDto.builder()
                .id(view.getId())
                .mainDescription(view.getMainDescription())
                .mainDescriptionTranslations(TranslationDto.of(HstoreParser.fromString(view.getMainDescriptionTranslations())))
                .furtherDescription(view.getFurtherDescription())
                .furtherDescriptionTranslations(TranslationDto.of(HstoreParser.fromString(view.getFurtherDescriptionTranslations())))
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}

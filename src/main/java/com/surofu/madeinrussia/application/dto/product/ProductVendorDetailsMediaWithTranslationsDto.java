package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.application.utils.HstoreParser;
import com.surofu.madeinrussia.infrastructure.persistence.product.vendorDetails.media.ProductVendorDetailsMediaWithTranslationsView;
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
public final class ProductVendorDetailsMediaWithTranslationsDto implements Serializable {

    private Long id;

    private String url;

    private String mediaType;

    private String altText;

    private TranslationDto altTextTranslations;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductVendorDetailsMediaWithTranslationsDto of(ProductVendorDetailsMediaWithTranslationsView view) {
        return ProductVendorDetailsMediaWithTranslationsDto.builder()
                .id(view.getId())
                .mediaType(view.getMediaType().getName())
                .url(view.getUrl())
                .altText(view.getAltText())
                .altTextTranslations(TranslationDto.of(HstoreParser.fromString(view.getAltTextTranslations())))
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}

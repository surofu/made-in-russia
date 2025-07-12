package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMedia;
import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ProductVendorDetailsMediaDto implements Serializable {

    private Long id;

    private String url;

    private String mediaType;

    private String altText;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;


    @Schema(hidden = true)
    public static ProductVendorDetailsMediaDto of(ProductVendorDetailsMedia productVendorDetailsMedia) {
        return ProductVendorDetailsMediaDto.builder()
                .id(productVendorDetailsMedia.getId())
                .url(productVendorDetailsMedia.getImage().getUrl())
                .mediaType(productVendorDetailsMedia.getMediaType().getName())
                .altText(productVendorDetailsMedia.getImage().getAltText())
                .creationDate(productVendorDetailsMedia.getCreationDate().getValue())
                .lastModificationDate(productVendorDetailsMedia.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static ProductVendorDetailsMediaDto of(ProductVendorDetailsMediaView view) {
        return ProductVendorDetailsMediaDto.builder()
                .id(view.getId())
                .mediaType(view.getMediaType().getName())
                .url(view.getImage().getUrl())
                .altText(view.getImage().getAltText())
                .creationDate(view.getCreationDate().getValue())
                .lastModificationDate(view.getLastModificationDate().getValue())
                .build();
    }
}

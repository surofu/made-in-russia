package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMedia;
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

    private String altText;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static ProductVendorDetailsMediaDto of(ProductVendorDetailsMedia productVendorDetailsMedia) {
        return ProductVendorDetailsMediaDto.builder()
                .id(productVendorDetailsMedia.getId())
                .url(productVendorDetailsMedia.getImage().getUrl())
                .altText(productVendorDetailsMedia.getImage().getAltText())
                .creationDate(productVendorDetailsMedia.getCreationDate().getValue())
                .lastModificationDate(productVendorDetailsMedia.getLastModificationDate().getValue())
                .build();
    }
}

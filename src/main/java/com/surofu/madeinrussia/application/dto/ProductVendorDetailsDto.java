package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ProductVendorDetailsDto implements Serializable {

    private Long id;

    private List<ProductVendorDetailsMediaDto> media;

    private String mainDescription;

    private String furtherDescription;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static ProductVendorDetailsDto of(ProductVendorDetails productVendorDetails) {
        return ProductVendorDetailsDto.builder()
                .id(productVendorDetails.getId())
                .media(productVendorDetails.getMedia().stream().map(ProductVendorDetailsMediaDto::of).toList())
                .mainDescription(productVendorDetails.getDescription().getMainDescription())
                .furtherDescription(productVendorDetails.getDescription().getFurtherDescription())
                .creationDate(productVendorDetails.getCreationDate().getValue())
                .lastModificationDate(productVendorDetails.getLastModificationDate().getValue())
                .build();
    }
}

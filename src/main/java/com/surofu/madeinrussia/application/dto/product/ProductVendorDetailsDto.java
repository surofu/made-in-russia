package com.surofu.madeinrussia.application.dto.product;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetails;
import com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.ProductVendorDetailsView;
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
public final class ProductVendorDetailsDto implements Serializable {

    private Long id;

    private List<ProductVendorDetailsMediaDto> media = new ArrayList<>();

    private String mainDescription;

    private String furtherDescription;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static ProductVendorDetailsDto of(ProductVendorDetails productVendorDetails) {
        if (productVendorDetails == null) {
            return null;
        }

        return ProductVendorDetailsDto.builder()
                .id(productVendorDetails.getId())
                .media(productVendorDetails.getMedia().stream().map(ProductVendorDetailsMediaDto::of).toList())
                .mainDescription(productVendorDetails.getDescription().getMainDescription())
                .furtherDescription(productVendorDetails.getDescription().getFurtherDescription())
                .creationDate(productVendorDetails.getCreationDate().getValue())
                .lastModificationDate(productVendorDetails.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static ProductVendorDetailsDto of(ProductVendorDetailsView view) {
        if (view == null) {
            return null;
        }

        return ProductVendorDetailsDto.builder()
                .id(view.getId())
                .mainDescription(view.getMainDescription())
                .furtherDescription(view.getFurtherDescription())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}

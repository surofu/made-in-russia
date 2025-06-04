package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.vendorProductCategory.VendorProductCategory;
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
public final class VendorProductCategoryDto implements Serializable {

    private Long id;

    private String name;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static VendorProductCategoryDto of(VendorProductCategory vendorProductCategory) {
        return VendorProductCategoryDto.builder()
                .id(vendorProductCategory.getId())
                .name(vendorProductCategory.getName().getValue())
                .creationDate(vendorProductCategory.getCreationDate().getValue())
                .lastModificationDate(vendorProductCategory.getLastModificationDate().getValue())
                .build();
    }
}

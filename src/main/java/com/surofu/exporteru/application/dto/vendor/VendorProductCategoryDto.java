package com.surofu.exporteru.application.dto.vendor;

import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.exporteru.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
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
@Schema(name = "VendorProductCategory")
public final class VendorProductCategoryDto implements Serializable {
    private Long id;
    private String name;
    private ZonedDateTime creationDate;
    private ZonedDateTime lastModificationDate;

    public static VendorProductCategoryDto of(VendorProductCategory vendorProductCategory) {
        if (vendorProductCategory == null) {
            return null;
        }
        return VendorProductCategoryDto.builder()
                .id(vendorProductCategory.getId())
                .name(vendorProductCategory.getName().getLocalizedValue())
                .creationDate(vendorProductCategory.getCreationDate().getValue())
                .lastModificationDate(vendorProductCategory.getLastModificationDate().getValue())
                .build();
    }

    public static VendorProductCategoryDto of(VendorProductCategoryView view) {
        if (view == null) {
            return null;
        }
        return VendorProductCategoryDto.builder()
                .id(view.getId())
                .name(view.getName())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
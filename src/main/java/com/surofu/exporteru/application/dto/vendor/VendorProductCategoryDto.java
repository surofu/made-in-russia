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
@Schema(
        name = "VendorProductCategory",
        description = "Represents a product category associated with a vendor",
        example = """
                {
                  "id": 5,
                  "name": "Electronics",
                  "creationDate": "2025-05-10T08:30:00Z",
                  "lastModificationDate": "2025-05-15T14:45:00Z"
                }
                """
)
public final class VendorProductCategoryDto implements Serializable {

    @Schema(
            description = "Unique identifier of the product category",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Name of the product category",
            example = "Electronics",
            minLength = 2,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Timestamp when the category was first associated with the vendor",
            example = "2025-05-10T08:30:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the category association was last modified",
            example = "2025-05-15T14:45:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static VendorProductCategoryDto of(VendorProductCategory vendorProductCategory) {
        if (vendorProductCategory == null) {
            return null;
        }

        return VendorProductCategoryDto.builder()
                .id(vendorProductCategory.getId())
                .name(vendorProductCategory.getName().getValue())
                .creationDate(vendorProductCategory.getCreationDate().getValue())
                .lastModificationDate(vendorProductCategory.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
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
package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
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
public final class VendorDetailsDto implements Serializable {

    private Long id;

    private String inn;

    private String companyName;

    private List<VendorCountryDto> countries;

    private List<VendorProductCategoryDto> productCategories;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static VendorDetailsDto of(VendorDetails vendorDetails) {
        if (vendorDetails == null) {
            return null;
        }

        return VendorDetailsDto.builder()
                .id(vendorDetails.getId())
                .inn(vendorDetails.getInn().getValue())
                .companyName(vendorDetails.getCompanyName().getValue())
                .countries(vendorDetails.getVendorCountries().stream().map(VendorCountryDto::of).toList())
                .creationDate(vendorDetails.getCreationDate().getValue())
                .productCategories(vendorDetails.getVendorProductCategories().stream().map(VendorProductCategoryDto::of).toList())
                .lastModificationDate(vendorDetails.getLastModificationDate().getValue())
                .build();
    }
}

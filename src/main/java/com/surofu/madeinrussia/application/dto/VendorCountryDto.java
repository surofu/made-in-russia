package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.vendorCountry.VendorCountry;
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
public final class VendorCountryDto implements Serializable {

    private Long id;

    private String name;

    private ZonedDateTime creationDate;

    private ZonedDateTime lastModificationDate;

    public static VendorCountryDto of(VendorCountry vendorCountry) {
        return VendorCountryDto.builder()
                .id(vendorCountry.getId())
                .name(vendorCountry.getName().getValue())
                .creationDate(vendorCountry.getCreationDate().getValue())
                .lastModificationDate(vendorCountry.getLastModificationDate().getValue())
                .build();
    }
}

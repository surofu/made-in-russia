package com.surofu.madeinrussia.application.dto.vendor;

import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.VendorCountryView;
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
        name = "VendorCountry",
        description = "Represents a country where the vendor operates or manufactures products",
        example = """
                {
                  "id": 1,
                  "name": "Russia",
                  "creationDate": "2025-04-20T10:00:00Z",
                  "lastModificationDate": "2025-05-10T15:30:00Z"
                }
                """
)
public final class VendorCountryDto implements Serializable {

    @Schema(
            description = "Unique identifier of the country association",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Name of the country where vendor operates",
            example = "Russia",
            minLength = 2,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Timestamp when the country was first associated with the vendor",
            example = "2025-04-20T10:00:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the country association was last modified",
            example = "2025-05-10T15:30:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static VendorCountryDto of(VendorCountry vendorCountry) {
        if (vendorCountry == null) {
            return null;
        }

        return VendorCountryDto.builder()
                .id(vendorCountry.getId())
                .name(vendorCountry.getName().getValue())
                .creationDate(vendorCountry.getCreationDate().getValue())
                .lastModificationDate(vendorCountry.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static VendorCountryDto of(VendorCountryView view) {
        if (view == null) {
            return null;
        }

        return VendorCountryDto.builder()
                .id(view.getId())
                .name(view.getName())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
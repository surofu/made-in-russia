package com.surofu.exporteru.application.dto.vendor;

import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.infrastructure.persistence.vendor.country.VendorCountryView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Locale;
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

    private String value;

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
    public static VendorCountryDto of(VendorCountry vendorCountry, Locale locale) {
        if (vendorCountry == null) {
            return null;
        }

        return VendorCountryDto.builder()
                .id(vendorCountry.getId())
                .name(vendorCountry.getName().getLocalizedValue(locale))
                .value(vendorCountry.getName().getTranslations().getOrDefault("en", vendorCountry.getName().toString()))
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
                .value(view.getValue())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }
}
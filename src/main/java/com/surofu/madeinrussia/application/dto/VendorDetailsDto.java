package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "VendorDetails",
        description = "Contains detailed information about a vendor including business registration and product categories",
        example = """
                {
                  "id": 789,
                  "inn": "7707083893",
                  "paymentDetails": "ЕРИП 12345АБВГ67890",
                  "countries": [
                    {
                      "id": 1,
                      "name": "Russia",
                      "creationDate": "2025-05-15T14:30:00Z",
                      "lastModificationDate": "2025-06-01T10:15:30Z"
                    }
                  ],
                  "productCategories": [
                    {
                      "id": 5,
                      "name": "Electronics",
                      "creationDate": "2025-05-15T14:30:00Z",
                      "lastModificationDate": "2025-06-01T10:15:30Z"
                    }
                  ],
                  "creationDate": "2025-05-15T14:30:00Z",
                  "lastModificationDate": "2025-06-01T10:15:30Z",
                  "viewsCount": "123"
                }
                """
)
public final class VendorDetailsDto implements Serializable {

    @Schema(
            description = "Unique identifier of the vendor details record",
            example = "789",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Vendor's Tax Identification Number (INN)",
            example = "7707083893",
            minLength = 7,
            maxLength = 12,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String inn;

    @Schema(
            description = "Vendor's payment details",
            example = "ЕРИП 12345АБВГД6890",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paymentDetails;

    @Schema(
            description = "List of countries where the vendor operates",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<VendorCountryDto> countries;

    @Schema(
            description = "List of product categories the vendor specializes in",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<VendorProductCategoryDto> productCategories;

    @Schema(
            description = "Count of profile views by authenticated users",
            example = "789",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long viewsCount = 0L;

    @Schema(
            description = "Timestamp when vendor details were initially created",
            example = "2025-05-15T14:30:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when vendor details were last modified",
            example = "2025-06-01T10:15:30Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    public static VendorDetailsDto of(VendorDetails vendorDetails) {
        if (vendorDetails == null) {
            return null;
        }

        return VendorDetailsDto.builder()
                .id(vendorDetails.getId())
                .inn(vendorDetails.getInn().getValue())
                .paymentDetails(vendorDetails.getPaymentDetails().getValue())
                .countries(vendorDetails.getVendorCountries().stream().map(VendorCountryDto::of).toList())
                .creationDate(vendorDetails.getCreationDate().getValue())
                .productCategories(vendorDetails.getVendorProductCategories().stream().map(VendorProductCategoryDto::of).toList())
                .viewsCount(vendorDetails.getVendorViewsCount())
                .lastModificationDate(vendorDetails.getLastModificationDate().getValue())
                .build();
    }
}
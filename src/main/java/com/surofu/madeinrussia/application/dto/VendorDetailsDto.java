package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "VendorDetails",
        description = "Contains comprehensive business information about a vendor including registration details, operational countries, product categories, and FAQs",
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
                  "faq": [
                    {
                      "id": 101,
                      "question": "What payment methods do you accept?",
                      "answer": "We accept all major credit cards and bank transfers",
                      "creationDate": "2025-05-15T14:30:00Z",
                      "lastModificationDate": "2025-06-01T10:15:30Z"
                    }
                  ],
                  "creationDate": "2025-05-15T14:30:00Z",
                  "lastModificationDate": "2025-06-01T10:15:30Z",
                  "viewsCount": 123
                }
                """
)
public final class VendorDetailsDto implements Serializable {

    @Schema(
            description = "Unique database identifier of the vendor details record",
            example = "789",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Vendor's Tax Identification Number (ИНН) as registered with Russian tax authorities",
            example = "7707083893",
            minLength = 10,
            maxLength = 12,
            pattern = "^[0-9]{10,12}$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String inn;

    @Schema(
            description = "Bank payment details for vendor transactions in ЕРИП system format",
            example = "ЕРИП 12345АБВГ6890",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paymentDetails;

    @Schema(
            description = "List of countries where the vendor has business operations",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<VendorCountryDto> countries;

    @Schema(
            description = "Product categories the vendor is authorized to sell in",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<VendorProductCategoryDto> productCategories;

    @Schema(
            description = "Frequently Asked Questions specific to this vendor's operations",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<VendorFaqDto> faq = new ArrayList<>();

    @Schema(
            description = "Counter of authenticated user profile views, automatically incremented",
            example = "789",
            minimum = "0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long viewsCount = 0L;

    @Schema(
            description = "System timestamp of initial vendor details registration",
            example = "2025-05-15T14:30:00Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "System timestamp of last modification to vendor details",
            example = "2025-06-01T10:15:30Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static VendorDetailsDto of(VendorDetails vendorDetails) {
        if (vendorDetails == null) {
            return null;
        }

        return VendorDetailsDto.builder()
                .id(vendorDetails.getId())
                .inn(vendorDetails.getInn().getValue())
                .paymentDetails(vendorDetails.getPaymentDetails().getValue())
                .countries(vendorDetails.getVendorCountries().stream().map(VendorCountryDto::of).toList())
                .productCategories(vendorDetails.getVendorProductCategories().stream().map(VendorProductCategoryDto::of).toList())
                .faq(vendorDetails.getFaq().stream().map(VendorFaqDto::of).toList())
                .viewsCount(vendorDetails.getVendorViewsCount())
                .creationDate(vendorDetails.getCreationDate().getValue())
                .lastModificationDate(vendorDetails.getLastModificationDate().getValue())
                .build();
    }
}
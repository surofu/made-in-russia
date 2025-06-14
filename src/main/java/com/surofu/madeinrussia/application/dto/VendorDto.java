package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Vendor",
        description = "Represents a vendor account with business details and authentication information",
        example = """
                {
                  "id": 12345,
                  "role": "ROLE_VENDOR",
                  "email": "vendor@example.com",
                  "login": "best_vendor_2025",
                  "phoneNumber": "+79123456789",
                  "vendorDetails": {
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
                    "lastModificationDate": "2025-06-01T10:15:30Z"
                  },
                  "registrationDate": "2025-05-04T09:17:20.767615Z",
                  "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                }
                """
)
@EqualsAndHashCode(callSuper = true)
public final class VendorDto extends AbstractAccountDto implements Serializable {

    @Schema(
            description = "Detailed business information about the vendor",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private VendorDetailsDto vendorDetails;

    @Schema(hidden = true)
    public static VendorDto of(User user) {
        if (user == null) {
            return null;
        }

        VendorDto vendorDto = new VendorDto();
        vendorDto.setId(user.getId());
        vendorDto.setRole(user.getRole().getName());
        vendorDto.setLogin(user.getLogin().getValue());
        vendorDto.setEmail(user.getEmail().getValue());
        vendorDto.setPhoneNumber(user.getPhoneNumber().getValue());
        vendorDto.setVendorDetails(VendorDetailsDto.of(user.getVendorDetails()));
        vendorDto.setRegistrationDate(user.getRegistrationDate().getValue());
        vendorDto.setLastModificationDate(user.getLastModificationDate().getValue());

        return vendorDto;
    }
}
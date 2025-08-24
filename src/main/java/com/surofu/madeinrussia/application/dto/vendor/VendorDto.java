package com.surofu.madeinrussia.application.dto.vendor;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.infrastructure.persistence.user.UserView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

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
                    "lastModificationDate": "2025-06-01T10:15:30Z",
                    "viewsCount": "123"
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
        vendorDto.setIsEnabled(user.getIsEnabled().getValue());
        vendorDto.setRole(user.getRole().getName());
        vendorDto.setLogin(user.getLogin().getValue());
        vendorDto.setEmail(user.getEmail().getValue());
        vendorDto.setPhoneNumber(StringUtils.trimToNull(Objects.requireNonNullElse(user.getPhoneNumber(), "").toString()));
        vendorDto.setVendorDetails(VendorDetailsDto.of(user.getVendorDetails()));
        vendorDto.setRegistrationDate(user.getRegistrationDate().getValue());
        vendorDto.setLastModificationDate(user.getLastModificationDate().getValue());

        return vendorDto;
    }

    @Schema(hidden = true)
    public static VendorDto of(UserView view) {
        if (view == null) {
            return null;
        }

        VendorDto vendorDto = new VendorDto();
        vendorDto.setId(view.getId());
        vendorDto.setIsEnabled(view.getIsEnabled().getValue());
        vendorDto.setEmail(view.getEmail().toString());
        vendorDto.setLogin(view.getLogin().toString());
        vendorDto.setPhoneNumber(StringUtils.trimToNull(Objects.requireNonNullElse(view.getPhoneNumber(), "").toString()));
        vendorDto.setRole(view.getRole().getName());
        vendorDto.setAvatarUrl(view.getAvatar() == null ? null : view.getAvatar().toString());
        vendorDto.setRegistrationDate(view.getRegistrationDate().getValue());
        vendorDto.setLastModificationDate(view.getLastModificationDate().getValue());
        vendorDto.setVendorDetails(VendorDetailsDto.of(view.getVendorDetails()));
        return vendorDto;
    }
}
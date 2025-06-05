package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "User",
        description = "Represents user information with authentication details and metadata",
        example = """
                {
                  "id": 12345,
                  "role": "User",
                  "email": "user@example.com",
                  "login": "john_doe",
                  "phoneNumber": "+79123456789",
                  "region": "Moscow, Russia",
                  "registrationDate": "2025-05-04T09:17:20.767615Z",
                  "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                }
                """
)
@EqualsAndHashCode(callSuper = true)
public final class VendorDto extends AbstractAccountDto implements Serializable {
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
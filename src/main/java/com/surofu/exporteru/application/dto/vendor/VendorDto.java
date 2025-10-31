package com.surofu.exporteru.application.dto.vendor;

import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.infrastructure.persistence.user.UserView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Vendor",
        description = "Represents a vendor account with business details and authentication information"
)
@EqualsAndHashCode(callSuper = true)
public final class VendorDto extends AbstractAccountDto implements Serializable {

    @Schema(
            description = "Detailed business information about the vendor",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private VendorDetailsDto vendorDetails;

    @Schema(hidden = true)
    public static VendorDto of(User user, Locale locale) {
        if (user == null) {
            return null;
        }

        VendorDto vendorDto = new VendorDto();
        vendorDto.setId(user.getId());
        vendorDto.setIsEnabled(user.getIsEnabled().getValue());
        vendorDto.setRole(user.getRole().getName());
        vendorDto.setLogin(getLocalizedLogin(user.getLogin(), locale));
        vendorDto.setEmail(user.getEmail().getValue());
        vendorDto.setPhoneNumber(StringUtils.trimToNull(Objects.requireNonNullElse(user.getPhoneNumber(), "").toString()));
        vendorDto.setVendorDetails(VendorDetailsDto.of(user.getVendorDetails(), locale));
        vendorDto.setAvatarUrl(user.getAvatar() == null ? null : user.getAvatar().getUrl());
        vendorDto.setRegistrationDate(user.getRegistrationDate().getValue());
        vendorDto.setLastModificationDate(user.getLastModificationDate().getValue());

        return vendorDto;
    }

    @Schema(hidden = true)
    public static VendorDto of(UserView view, Locale locale) {
        if (view == null) {
            return null;
        }

        VendorDto vendorDto = new VendorDto();
        vendorDto.setId(view.getId());
        vendorDto.setIsEnabled(view.getIsEnabled().getValue());
        vendorDto.setEmail(view.getEmail().toString());
        vendorDto.setLogin(getLocalizedLogin(view.getLogin(), locale));
        vendorDto.setPhoneNumber(StringUtils.trimToNull(Objects.requireNonNullElse(view.getPhoneNumber(), "").toString()));
        vendorDto.setRole(view.getRole().getName());
        vendorDto.setAvatarUrl(view.getAvatar() == null ? null : view.getAvatar().toString());
        vendorDto.setRegistrationDate(view.getRegistrationDate().getValue());
        vendorDto.setLastModificationDate(view.getLastModificationDate().getValue());
        vendorDto.setVendorDetails(VendorDetailsDto.of(view.getVendorDetails(), locale));
        return vendorDto;
    }

    private static String getLocalizedLogin(UserLogin login, Locale locale) {
        if (login.getTransliteration() == null) {
            return login.toString();
        }

        return switch (locale.getLanguage()) {
            case "en" -> login.getTransliteration().textEn();
            case "ru" -> login.getTransliteration().textRu();
            case "zh" -> login.getTransliteration().textZh();
            default -> login.getTransliteration().textEn();
        };
    }
}
package com.surofu.exporteru.application.dto.user;

import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.infrastructure.persistence.user.UserView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "User",
        description = "Represents user information with authentication details and metadata"
)
@EqualsAndHashCode(callSuper = true)
public class UserDto extends AbstractAccountDto implements Serializable {
    @Schema(
            description = "User's geographical region or location",
            example = "Moscow, Russia",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String region;

    @Schema(hidden = true)
    public static UserDto of(User user, Locale locale) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setIsEnabled(user.getIsEnabled().getValue());
        userDto.setRole(user.getRole().getName());
        userDto.setLogin(getLocalizedLogin(user.getLogin(), locale));
        userDto.setEmail(user.getEmail().getValue());
        userDto.setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber().getValue() : null);
        userDto.setRegion(user.getRegion().getValue());
        userDto.setAvatarUrl(user.getAvatar() != null ? user.getAvatar().getUrl() : null);
        userDto.setRegistrationDate(user.getRegistrationDate().getValue());
        userDto.setLastModificationDate(user.getLastModificationDate().getValue());

        return userDto;
    }

    @Schema(hidden = true)
    public static UserDto of(UserView view, Locale locale) {
        if (view == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(view.getId());
        userDto.setIsEnabled(view.getIsEnabled().getValue());
        userDto.setRole(view.getRole().getName());
        userDto.setLogin(getLocalizedLogin(view.getLogin(), locale));
        userDto.setEmail(view.getEmail().getValue());
        userDto.setPhoneNumber(view.getPhoneNumber() != null ? view.getPhoneNumber().getValue() : null);
        userDto.setRegion(view.getRegion().getValue());
        userDto.setAvatarUrl(view.getAvatar() != null ? view.getAvatar().getUrl() : null);
        userDto.setRegistrationDate(view.getRegistrationDate().getValue());
        userDto.setLastModificationDate(view.getLastModificationDate().getValue());

        return userDto;
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
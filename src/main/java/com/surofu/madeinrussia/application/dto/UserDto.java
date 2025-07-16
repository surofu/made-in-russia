package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.infrastructure.persistence.user.UserView;
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
public final class UserDto extends AbstractAccountDto implements Serializable {
    @Schema(
            description = "User's geographical region or location",
            example = "Moscow, Russia",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String region;

    @Schema(hidden = true)
    public static UserDto of(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setRole(user.getRole().getName());
        userDto.setLogin(user.getLogin().getValue());
        userDto.setEmail(user.getEmail().getValue());
        userDto.setPhoneNumber(user.getPhoneNumber().getValue());
        userDto.setRegion(user.getRegion().getValue());
        userDto.setRegistrationDate(user.getRegistrationDate().getValue());
        userDto.setLastModificationDate(user.getLastModificationDate().getValue());

        return userDto;
    }

    @Schema(hidden = true)
    public static UserDto of(UserView view) {
        if (view == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(view.getId());
        userDto.setRole(view.getRole().getName());
        userDto.setLogin(view.getLogin().getValue());
        userDto.setEmail(view.getEmail().getValue());
        userDto.setPhoneNumber(view.getPhoneNumber().getValue());
        userDto.setRegion(view.getRegion().getValue());
        userDto.setRegistrationDate(view.getRegistrationDate().getValue());
        userDto.setLastModificationDate(view.getLastModificationDate().getValue());

        return userDto;
    }
}
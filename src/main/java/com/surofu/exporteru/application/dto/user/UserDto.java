package com.surofu.exporteru.application.dto.user;

import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.infrastructure.persistence.user.UserView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
  public static UserDto of(User user) {
    if (user == null) {
      return null;
    }

    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setIsEnabled(user.getIsEnabled().getValue());
    userDto.setRole(user.getRole().getName());
    userDto.setLogin(user.getLogin().getLocalizedValue());
    userDto.setEmail(user.getEmail().getValue());
    userDto.setPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber().getValue() : null);
    userDto.setRegion(user.getRegion().getValue());
    userDto.setAvatarUrl(user.getAvatar() != null ? user.getAvatar().getUrl() : null);
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
    userDto.setIsEnabled(view.getIsEnabled().getValue());
    userDto.setRole(view.getRole().getName());
    userDto.setLogin(view.getLogin().getLocalizedValue());
    userDto.setEmail(view.getEmail().getValue());
    userDto.setPhoneNumber(view.getPhoneNumber() != null ? view.getPhoneNumber().getValue() : null);
    userDto.setRegion(view.getRegion().getValue());
    userDto.setAvatarUrl(view.getAvatar() != null ? view.getAvatar().getUrl() : null);
    userDto.setRegistrationDate(view.getRegistrationDate().getValue());
    userDto.setLastModificationDate(view.getLastModificationDate().getValue());

    return userDto;
  }
}
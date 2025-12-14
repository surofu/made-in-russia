package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserPhoneNumber implements Serializable {
  @Column(name = "phone_number", unique = true)
  private String value;

  public UserPhoneNumber(String phoneNumber) {
    if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
      if (phoneNumber.length() < 7) {
        throw new LocalizedValidationException("validation.phone_number.min_length");
      }
      if (phoneNumber.length() > 255) {
        throw new LocalizedValidationException("validation.phone_number.max_length");
      }
    }
    this.value = StringUtils.trimToNull(phoneNumber);
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserPhoneNumber that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

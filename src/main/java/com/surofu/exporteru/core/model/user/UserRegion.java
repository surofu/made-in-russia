package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class UserRegion implements Serializable {
  @Column(name = "region", nullable = false)
  private String value;

  public UserRegion(String region) {
    if (region == null || region.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.user.region.empty");
    }
    if (region.length() > 255) {
      throw new LocalizedValidationException("validation.user.region.max_length");
    }
    this.value = region;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserRegion that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

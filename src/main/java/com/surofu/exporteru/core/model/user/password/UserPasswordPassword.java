package com.surofu.exporteru.core.model.user.password;

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
public final class UserPasswordPassword implements Serializable {
  @Column(name = "password", nullable = false, columnDefinition = "text")
  private String value;

  public UserPasswordPassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.password.empty");
    }
    if (password.length() < 4) {
      throw new LocalizedValidationException("validation.password.min_length");
    }
    if (password.length() > 10_000) {
      throw new LocalizedValidationException("validation.password.max_length");
    }
    this.value = password;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserPasswordPassword that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
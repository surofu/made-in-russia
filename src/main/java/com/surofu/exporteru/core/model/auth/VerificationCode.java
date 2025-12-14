package com.surofu.exporteru.core.model.auth;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VerificationCode implements Serializable {
  private String value;

  public VerificationCode(String code) {
    if (code == null || code.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.verification_code.empty");
    }
    if (code.length() > 255) {
      throw new LocalizedValidationException("validation.verification_code.max_length");
    }
    this.value = code;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VerificationCode that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

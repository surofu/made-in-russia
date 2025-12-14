package com.surofu.exporteru.core.model.session;

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
public final class SessionOs implements Serializable {
  @Column(name = "os", nullable = false, updatable = false)
  private String value;

  public SessionOs(String os) {
    if (os == null || os.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.session.os.empty");
    }
    if (os.length() > 255) {
      throw new LocalizedValidationException("validation.session.os.max_length");
    }
    this.value = os;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SessionOs sessionOs)) {
      return false;
    }
    return Objects.equals(value, sessionOs.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

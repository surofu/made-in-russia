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
public final class SessionDeviceType implements Serializable {
  @Column(name = "device_type", nullable = false, updatable = false)
  private String value;

  public SessionDeviceType(String deviceType) {
    if (deviceType == null || deviceType.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.session.device_type.empty");
    }
    if (deviceType.length() > 255) {
      throw new LocalizedValidationException("validation.session.device_type.max_length");
    }
    this.value = deviceType;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SessionDeviceType that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

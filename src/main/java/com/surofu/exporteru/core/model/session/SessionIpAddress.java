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
public final class SessionIpAddress implements Serializable {
  @Column(name = "ip_address", nullable = false, updatable = false)
  private String value;

  public SessionIpAddress(String ipAddress) {
    if (ipAddress == null || ipAddress.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.session.ip.empty");
    }
    this.value = ipAddress;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SessionIpAddress that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

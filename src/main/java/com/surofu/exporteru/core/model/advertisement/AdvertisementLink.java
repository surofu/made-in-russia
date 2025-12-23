package com.surofu.exporteru.core.model.advertisement;

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
public final class AdvertisementLink implements Serializable {
  @Column(name = "link")
  private String value;

  public AdvertisementLink(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.external_link.empty");
    }
    if (url.length() > 20_000) {
      throw new LocalizedValidationException("validation.external_link.max_length");
    }
    this.value = url;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AdvertisementLink that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

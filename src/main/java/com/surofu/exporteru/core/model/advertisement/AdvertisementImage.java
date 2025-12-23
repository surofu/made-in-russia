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
public final class AdvertisementImage implements Serializable {
  @Column(name = "image_url", nullable = false, columnDefinition = "text")
  private String url;

  public AdvertisementImage(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.image_url.empty");
    }
    if (url.length() > 20_000) {
      throw new LocalizedValidationException("validation.image_url.max_length");
    }
    this.url = url;
  }

  @Override
  public String toString() {
    return url;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AdvertisementImage that)) {
      return false;
    }
    return Objects.equals(url, that.url);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(url);
  }
}

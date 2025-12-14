package com.surofu.exporteru.core.model.product.media;

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
public final class ProductMediaUrl implements Serializable {
  @Column(name = "url", nullable = false, columnDefinition = "text")
  private String value;

  public ProductMediaUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.media.url.empty");
    }
    if (url.length() >= 20_000) {
      throw new LocalizedValidationException("validation.media.url.max_length");
    }
    this.value = url;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductMediaUrl that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

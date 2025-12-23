package com.surofu.exporteru.core.model.product.review.media;

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
public final class ProductReviewMediaMimeType implements Serializable {
  @Column(name = "mime_type", nullable = false)
  private String value;

  public ProductReviewMediaMimeType(String mimeType) {
    if (mimeType == null || mimeType.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.media.mime_type.empty");
    }
    if (mimeType.length() > 255) {
      throw new LocalizedValidationException("validation.media.mime_type.max_length");
    }
    this.value = mimeType;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductReviewMediaMimeType that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

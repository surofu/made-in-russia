package com.surofu.exporteru.core.model.product.review.media;

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
public final class ProductReviewMediaMediaPosition implements Serializable {
  @Column(name = "position", nullable = false, columnDefinition = "int default 0")
  private Integer value = 0;

  public ProductReviewMediaMediaPosition(Integer position) {
    if (position < 0) {
      throw new IllegalArgumentException("vendor.media.invalid_position");
    }
    this.value = position;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductReviewMediaMediaPosition that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

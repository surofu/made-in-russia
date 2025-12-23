package com.surofu.exporteru.core.model.product.media;

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
public final class ProductMediaPosition implements Serializable {
  @Column(name = "position", nullable = false, columnDefinition = "int default 0")
  private Integer value = 0;

  public ProductMediaPosition(Integer position) {
    if (position == null) {
      throw new IllegalArgumentException("Позиция медиа не может быть пустой");
    }
    if (position < 0) {
      throw new IllegalArgumentException("Позиция медиа не может быть отрицательной");
    }
    this.value = position;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductMediaPosition that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

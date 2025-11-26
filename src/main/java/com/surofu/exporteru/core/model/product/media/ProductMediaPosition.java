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

  private ProductMediaPosition(Integer position) {
    if (position == null) {
      throw new IllegalArgumentException("Позиция медиа не может быть пустой");
    }

    if (position < 0) {
      throw new IllegalArgumentException("Позиция медиа не может быть отрицательной");
    }

    this.value = position;
  }

  public static ProductMediaPosition of(Integer position) {
    return new ProductMediaPosition(position);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (!(o instanceof ProductMediaPosition productMediaPosition)) {
          return false;
      }
    return Objects.equals(value, productMediaPosition.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

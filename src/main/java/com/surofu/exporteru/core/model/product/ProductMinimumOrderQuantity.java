package com.surofu.exporteru.core.model.product;

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
public final class ProductMinimumOrderQuantity implements Serializable {
  @Column(name = "minimum_order_quantity", columnDefinition = "int")
  private Integer value;

  public ProductMinimumOrderQuantity(Integer minimumOrderQuantity) {
    if (minimumOrderQuantity == null) {
      throw new IllegalArgumentException("Минимальное количество товара не может быть пустым");
    }
    if (minimumOrderQuantity < 0) {
      throw new IllegalArgumentException(
          "Минимальное количество товара не может быть отрицательным");
    }
    this.value = minimumOrderQuantity;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductMinimumOrderQuantity that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

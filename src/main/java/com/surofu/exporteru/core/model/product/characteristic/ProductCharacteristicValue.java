package com.surofu.exporteru.core.model.product.characteristic;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductCharacteristicValue implements Serializable {

  @Column(name = "value", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "value_translations")
  private Map<String, String> translations = new HashMap<>();

  private ProductCharacteristicValue(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Значение характеристики не может быть пустой");
    }

    if (value.length() > 255) {
      throw new IllegalArgumentException(
          "Значение характеристики не может быть больше 255 символов");
    }

    this.value = value;
  }

  public static ProductCharacteristicValue of(String value) {
    return new ProductCharacteristicValue(value);
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProductCharacteristicValue productCharacteristicValue)) return false;
    return Objects.equals(value, productCharacteristicValue.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}

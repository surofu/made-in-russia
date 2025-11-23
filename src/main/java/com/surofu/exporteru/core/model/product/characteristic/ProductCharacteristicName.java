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
public final class ProductCharacteristicName implements Serializable {

  @Column(name = "name", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private Map<String, String> translations = new HashMap<>();

  private ProductCharacteristicName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Название характеристики не может быть пустой");
    }

    if (name.length() > 255) {
      throw new IllegalArgumentException(
          "Название характеристики не может быть больше 255 символов");
    }

    this.value = name;
  }

  public static ProductCharacteristicName of(String name) {
    return new ProductCharacteristicName(name);
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProductCharacteristicName productCharacteristicName)) return false;
    return Objects.equals(value, productCharacteristicName.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}

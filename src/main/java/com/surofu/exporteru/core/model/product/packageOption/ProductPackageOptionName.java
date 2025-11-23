package com.surofu.exporteru.core.model.product.packageOption;

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
public final class ProductPackageOptionName implements Serializable {

  @Column(name = "name", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private Map<String, String> translations = new HashMap<>();

  // TODO: Translation
  private ProductPackageOptionName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Название варианта упаковки товара не может быть пустым");
    }

    if (name.length() > 255) {
      throw new IllegalArgumentException(
          "Название варианта упаковки товара не может быть больше 255 символов");
    }

    this.value = name;
  }

  public static ProductPackageOptionName of(String name) {
    return new ProductPackageOptionName(name);
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProductPackageOptionName productPackageOptionName)) {
      return false;
    }
    return Objects.equals(value, productPackageOptionName.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}

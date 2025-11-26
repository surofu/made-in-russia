package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceUnit implements Serializable {

  @Column(name = "quantity_unit", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "unit_translations")
  private Map<String, String> translations;

  public ProductPriceUnit(String unit, Map<String, String> translations) {
    if (unit == null || unit.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.product.price.unit.empty");
    }

    if (unit.length() > 255) {
      throw new LocalizedValidationException("validation.product.price.unit.max_length");
    }

    this.value = unit;
    this.translations = translations;
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
      if (!(o instanceof ProductPriceUnit productPriceUnit)) {
          return false;
      }
    return Objects.equals(value, productPriceUnit.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

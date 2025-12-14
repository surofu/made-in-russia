package com.surofu.exporteru.core.model.product.packageOption;

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
public final class ProductPackageOptionPriceUnit implements Serializable {
  @Column(name = "price_unit", nullable = false)
  private String value;

  public ProductPackageOptionPriceUnit(String priceUnit) {
    if (priceUnit == null || priceUnit.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Валюта цены в варианте упаковки товара не может быть пустой");
    }
    this.value = priceUnit;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductPackageOptionPriceUnit that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

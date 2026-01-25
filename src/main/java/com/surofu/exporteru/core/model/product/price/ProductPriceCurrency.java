package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import com.surofu.exporteru.core.model.currency.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPriceCurrency implements Serializable {
  @Enumerated(EnumType.STRING)
  @Column(name = "currency", nullable = false, columnDefinition = "currency")
  private CurrencyCode value;

  public ProductPriceCurrency(String currencyString) {
    if (currencyString == null || currencyString.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.product.price.currency.empty");
    }
    CurrencyCode currencyCode;
    try {
      if ("notNumberCurrency".equals(currencyString)) {
        currencyCode = CurrencyCode.NO_CURRENCY;
      } else {
        currencyCode = CurrencyCode.valueOf(currencyString);
      }
    } catch (IllegalArgumentException e) {
      throw new LocalizedValidationException("validation.product.price.currency.type",
          currencyString);
    }
    this.value = currencyCode;
  }

  @Override
  public String toString() {
    return value.name();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductPriceCurrency that)) {
      return false;
    }
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

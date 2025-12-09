package com.surofu.exporteru.core.model.product.price;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class ProductPriceUnit implements Serializable {
  @Column(name = "quantity_unit", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "unit_translations")
  private final Map<String, String> translations;

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

  public ProductPriceUnit(String unit) {
    this(unit, null);
  }

  public ProductPriceUnit() {
    this.value = null;
    this.translations = new HashMap<>();
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }

    Locale locale = LocaleContextHolder.getLocale();
    return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductPriceUnit that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

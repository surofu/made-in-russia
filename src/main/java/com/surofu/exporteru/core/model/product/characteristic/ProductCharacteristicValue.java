package com.surofu.exporteru.core.model.product.characteristic;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class ProductCharacteristicValue implements Serializable {

  @Column(name = "value", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "value_translations")
  private final Map<String, String> translations;

  public ProductCharacteristicValue(String value, Map<String, String> translations) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Значение характеристики не может быть пустой");
    }

    if (value.length() > 255) {
      throw new IllegalArgumentException(
          "Значение характеристики не может быть больше 255 символов");
    }

    this.value = value;
    this.translations = translations;
  }

  public ProductCharacteristicValue() {
    this("Value", new HashMap<>());
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }

    Locale locale = LocaleContextHolder.getLocale();
    return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProductCharacteristicValue productCharacteristicValue)) return false;
    return Objects.equals(value, productCharacteristicValue.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

package com.surofu.exporteru.core.model.product.characteristic;

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
public final class ProductCharacteristicName implements Serializable {

  @Column(name = "name", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private final Map<String, String> translations;

  public ProductCharacteristicName(String name, Map<String, String> translations) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Название характеристики не может быть пустой");
    }

    if (name.length() > 255) {
      throw new IllegalArgumentException(
          "Название характеристики не может быть больше 255 символов");
    }

    this.value = name;
    this.translations = translations;
  }

  public ProductCharacteristicName() {
    this("Name", new HashMap<>());
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
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProductCharacteristicName productCharacteristicName)) {
      return false;
    }
    return Objects.equals(value, productCharacteristicName.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

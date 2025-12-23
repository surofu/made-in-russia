package com.surofu.exporteru.core.model.product.characteristic;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductCharacteristicValue implements Serializable {
  @Column(name = "value", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "value_translations")
  private Map<String, String> translations;

  public ProductCharacteristicValue(String value, Map<String, String> translations) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Значение характеристики не может быть пустой");
    }
    if (value.length() > 255) {
      throw new IllegalArgumentException(
          "Значение характеристики не может быть больше 255 символов");
    }
    this.value = value;
    this.translations = translations != null
        ? new HashMap<>(translations)
        : new HashMap<>();
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
  }

  public Map<String, String> getTranslations() {
    return translations != null
        ? Collections.unmodifiableMap(translations)
        : Collections.emptyMap();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductCharacteristicValue that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

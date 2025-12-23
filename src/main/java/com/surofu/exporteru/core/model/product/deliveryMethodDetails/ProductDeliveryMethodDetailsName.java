package com.surofu.exporteru.core.model.product.deliveryMethodDetails;

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
public final class ProductDeliveryMethodDetailsName implements Serializable {
  @Column(name = "name", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private Map<String, String> translations;

  public ProductDeliveryMethodDetailsName(String name, Map<String, String> translations) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Название способа доставки товара не может быть пустым");
    }

    this.value = name;
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
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductDeliveryMethodDetailsName that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

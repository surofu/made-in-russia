package com.surofu.exporteru.core.model.product.media;

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
public final class ProductMediaAltText implements Serializable {

  @Column(name = "alt_text", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "alt_text_translations")
  private final Map<String, String> translations;

  public ProductMediaAltText(String altText, Map<String, String> translations) {
    if (altText == null || altText.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Альтернативное описание медиа товара не может быть пустым");
    }

    if (altText.length() > 255) {
      throw new IllegalArgumentException(
          "Альтернативное описание медиа товара не может быть больше 255 символов");
    }

    this.value = altText;
    this.translations = translations;
  }

  public ProductMediaAltText() {
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
      if (this == o) {
          return true;
      }
      if (!(o instanceof ProductMediaAltText productMediaAltText)) {
          return false;
      }
    return Objects.equals(value, productMediaAltText.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

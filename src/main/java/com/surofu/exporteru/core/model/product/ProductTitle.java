package com.surofu.exporteru.core.model.product;

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
public final class ProductTitle implements Serializable {
  @Column(name = "title", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "title_translations")
  private final Map<String, String> translations;

  public ProductTitle(String value, Map<String, String> translations) {
    if (value == null || value.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.product.title.empty");
    }

    if (value.length() > 255) {
      throw new LocalizedValidationException("validation.product.title.max_length");
    }
    this.value = value;
    this.translations = translations;
  }

  public ProductTitle(String value) {
    this(value, new HashMap<>());
  }

  public ProductTitle() {
    this("Title");
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }

    Locale locale = LocaleContextHolder.getLocale();
    return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (ProductTitle) obj;
    return Objects.equals(this.value, that.value) &&
        Objects.equals(this.translations, that.translations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, translations);
  }
}
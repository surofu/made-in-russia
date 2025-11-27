package com.surofu.exporteru.core.model.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class CategoryMetaDescription implements Serializable {

  @Column(name = "meta_description")
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "meta_description_translations")
  private final Map<String, String> translations;

  public CategoryMetaDescription(String value, Map<String, String> translations) {
    this.value = value;
    this.translations = translations;
  }

  public CategoryMetaDescription() {
    this.value = null;
    this.translations = null;
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
    if (this == o) {
      return true;
    }
    if (!(o instanceof CategoryMetaDescription categoryMetaDescription)) {
      return false;
    }
    return Objects.equals(value, categoryMetaDescription.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

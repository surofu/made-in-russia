package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class CategoryTitle implements Serializable {

  @Column(name = "title", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "title_translations", nullable = false)
  private final Map<String, String> translations;

  public CategoryTitle(String value, Map<String, String> translations) {
    if (StringUtils.trimToNull(value) == null) {
      throw new LocalizedValidationException("validation.category.title.empty");
    }

    if (value.length() > 255) {
      throw new LocalizedValidationException("validation.category.title.max_length");
    }

    this.value = value;
    this.translations = translations;
  }

  public CategoryTitle() {
    this.value = null;
    this.translations = null;
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return Objects.requireNonNullElse(translations.get(locale.getLanguage()),
        Objects.requireNonNullElse(value, ""));
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
    if (!(o instanceof CategoryTitle categoryTitle)) {
      return false;
    }
    return Objects.equals(value, categoryTitle.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

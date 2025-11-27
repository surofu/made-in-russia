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
public final class CategoryName implements Serializable {

  @Column(name = "name", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations", nullable = false)
  private final Map<String, String> translations;

  public CategoryName(String name, Map<String, String> translations) {
    if (StringUtils.trimToNull(name) == null) {
      throw new LocalizedValidationException("validation.category.name.empty");
    }

    if (name.length() > 255) {
      throw new LocalizedValidationException("validation.category.name.max_length");
    }

    this.value = name;
    this.translations = translations;
  }

  public CategoryName() {
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
    if (!(o instanceof CategoryName categoryName)) {
      return false;
    }
    return Objects.equals(value, categoryName.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryLabel implements Serializable {
  @Column(name = "label", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "label_translations", nullable = false)
  private Map<String, String> translations;

  public CategoryLabel(String value, Map<String, String> translations) {
    if (StringUtils.trimToNull(value) == null) {
      throw new LocalizedValidationException("validation.category.label.empty");
    }

    if (value.length() > 255) {
      throw new LocalizedValidationException("validation.category.label.max_length");
    }

    this.value = value;
    this.translations = translations != null
        ? Collections.unmodifiableMap(translations)
        : Collections.emptyMap();
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return Objects.requireNonNullElse(translations.get(locale.getLanguage()),
        Objects.requireNonNullElse(value, ""));
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
    if (!(o instanceof CategoryLabel that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}

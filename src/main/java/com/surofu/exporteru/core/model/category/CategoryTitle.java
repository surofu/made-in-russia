package com.surofu.exporteru.core.model.category;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
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
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryTitle implements Serializable {

  @Column(name = "title", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "title_translations", nullable = false)
  private Map<String, String> translations = new HashMap<>();

  private CategoryTitle(String title) {
    if (StringUtils.trimToNull(title) == null) {
      throw new LocalizedValidationException("validation.category.title.empty");
    }

    if (title.length() > 255) {
      throw new LocalizedValidationException("validation.category.title.max_length");
    }

    this.value = title;
  }

  public static CategoryTitle of(String title) {
    return new CategoryTitle(title);
  }

  public String getLocalizedValue(Locale locale) {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    return Objects.requireNonNullElse(translations.get(locale.getLanguage()), Objects.requireNonNullElse(value, ""));
  }

  @Override
  public String toString() {
    return value;
  }
}

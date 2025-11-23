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
public final class CategoryLabel implements Serializable {

  @Column(name = "label", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "label_translations", nullable = false)
  private Map<String, String> translations = new HashMap<>();

  private CategoryLabel(String label) {
    if (StringUtils.trimToNull(label) == null) {
      throw new LocalizedValidationException("validation.category.label.empty");
    }

    if (label.length() > 255) {
      throw new LocalizedValidationException("validation.category.label.max_length");
    }

    this.value = label;
  }

  public static CategoryLabel of(String label) {
    return new CategoryLabel(label);
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

package com.surofu.exporteru.core.model.category;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CategoryDescription implements Serializable {

  @Column(name = "description")
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "description_translations")
  private Map<String, String> translations = new HashMap<>();

  private CategoryDescription(String description) {
    this.value = description;
  }

  public static CategoryDescription of(String description) {
    return new CategoryDescription(description);
  }

  public String getLocalizedValue(Locale locale) {
    return Objects.requireNonNullElse(translations.get(locale.getLanguage()), Objects.requireNonNullElse(value, ""));
  }

  @Override
  public String toString() {
    return value;
  }
}

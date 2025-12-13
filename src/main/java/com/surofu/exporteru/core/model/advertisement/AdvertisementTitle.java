package com.surofu.exporteru.core.model.advertisement;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AdvertisementTitle implements Serializable {

  @Column(name = "title", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "title_translations", nullable = false)
  private Map<String, String> translations = new HashMap<>();

  private AdvertisementTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.title.empty");
    }

    if (title.length() > 255) {
      throw new LocalizedValidationException("validation.title.max_length");
    }

    this.value = title;
  }

  public String getLocalizedValue(Locale locale) {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    return Objects.requireNonNullElse(translations.get(locale.getLanguage()), Objects.requireNonNullElse(value, ""));
  }

  public static AdvertisementTitle of(String title) {
    return new AdvertisementTitle(title);
  }

  @Override
  public String toString() {
    return value;
  }
}

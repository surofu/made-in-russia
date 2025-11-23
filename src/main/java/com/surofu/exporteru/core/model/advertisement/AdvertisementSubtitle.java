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
public final class AdvertisementSubtitle implements Serializable {

  @Column(name = "subtitle", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "subtitle_translations", nullable = false, columnDefinition = "hstore")
  private Map<String, String> translations = new HashMap<>();

  private AdvertisementSubtitle(String subtitle) {
    if (subtitle == null || subtitle.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.subtitle.empty");
    }

    if (subtitle.length() > 255) {
      throw new LocalizedValidationException("validation.subtitle.max_length");
    }

    this.value = subtitle;
  }

  public String getLocalizedValue(Locale locale) {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    return Objects.requireNonNullElse(translations.get(locale.getLanguage()), Objects.requireNonNullElse(value, ""));
  }

  public static AdvertisementSubtitle of(String subtitle) {
    return new AdvertisementSubtitle(subtitle);
  }

  @Override
  public String toString() {
    return value;
  }
}

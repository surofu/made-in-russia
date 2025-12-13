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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AdvertisementThirdText implements Serializable {

  @Column(name = "third_text")
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "third_text_translations")
  private Map<String, String> translations = new HashMap<>();

  public AdvertisementThirdText(String text, Map<String, String> translations) {
    if (text != null && text.length() > 255) {
      throw new LocalizedValidationException("validation.third_title.max_length");
    }

    this.value = text;
    this.translations = translations;
  }

  public String getLocalizedValue(Locale locale) {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
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
      if (!(o instanceof AdvertisementThirdText advertisementThirdText)) {
          return false;
      }
    return Objects.equals(value, advertisementThirdText.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

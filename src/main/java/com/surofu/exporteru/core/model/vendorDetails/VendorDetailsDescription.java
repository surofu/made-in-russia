package com.surofu.exporteru.core.model.vendorDetails;

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
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class VendorDetailsDescription implements Serializable {
  @Column(name = "description", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "description_translations")
  private final Map<String, String> translations;

  public VendorDetailsDescription(String text, Map<String, String> translations) {
    if (text != null && text.length() > 20_000) {
      throw new LocalizedValidationException("validation.vendor.description.max_length");
    }

    this.value = Objects.requireNonNullElse(text, "");
    this.translations = translations;
  }

  public VendorDetailsDescription(String text) {
    this(text, new HashMap<>());
  }

  public VendorDetailsDescription() {
    this.value = null;
    this.translations = new HashMap<>();
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
    if (!(o instanceof VendorDetailsDescription that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

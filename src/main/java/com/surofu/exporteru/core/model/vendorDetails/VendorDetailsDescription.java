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
@Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsDescription implements Serializable {

  @Getter(AccessLevel.NONE)
  @Column(name = "description", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "description_translations")
  private Map<String, String> translations = new HashMap<>();

  private VendorDetailsDescription(String text) {
    if (text != null && text.length() > 20_000) {
      throw new LocalizedValidationException("validation.vendor.description.max_length");
    }

    this.value = Objects.requireNonNullElse(text, "");
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
  }

  public static VendorDetailsDescription of(String text) {
    return new VendorDetailsDescription(text);
  }

  @Override
  public String toString() {
    return value;
  }
}

package com.surofu.exporteru.core.model.deliveryMethod;

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
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class DeliveryMethodName implements Serializable {

  @Column(name = "name", unique = true, nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private final Map<String, String> translations;

  public DeliveryMethodName(String name, Map<String, String> translations) {
    if (name == null || name.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.delivery_method.name.empty");
    }

    if (name.length() > 255) {
      throw new LocalizedValidationException("validation.delivery_method.name.max_length");
    }

    this.value = name;
    this.translations = translations;
  }

  public DeliveryMethodName(String name) {
    this(name, new HashMap<>());
  }

  public DeliveryMethodName() {
    this.value = null;
    this.translations = new HashMap<>();
  }

  public String getLocalizedValue() {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return translations.getOrDefault(locale.getLanguage(),
        Objects.requireNonNullElse(value, ""));
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DeliveryMethodName that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

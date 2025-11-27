package com.surofu.exporteru.core.model.deliveryTerm;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class DeliveryTermName implements Serializable {

  @Column(name = "name", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private final Map<String, String> translations;

  public DeliveryTermName(String value, Map<String, String> translations) {
    if (StringUtils.trimToNull(value) == null) {
      throw new IllegalArgumentException("validation.delivery_term.name.empty");
    }

    if (value.length() > 255) {
      throw new IllegalArgumentException("validation.delivery_term.name.max_length");
    }

    this.value = value;
    this.translations = translations;
  }

  public DeliveryTermName() {
    this("NAME", new HashMap<>());
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
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeliveryTermName deliveryTermName)) {
      return false;
    }
    return Objects.equals(value, deliveryTermName.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

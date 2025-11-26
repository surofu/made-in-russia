package com.surofu.exporteru.core.model.product.packageOption;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductPackageOptionName implements Serializable {

  @Column(name = "name", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private Map<String, String> translations;

  public ProductPackageOptionName(String name, Map<String, String> translations) {
    if (StringUtils.trimToNull(name) == null) {
      throw new IllegalArgumentException("Название варианта упаковки товара не может быть пустым");
    }

    if (name.length() > 255) {
      throw new IllegalArgumentException(
          "Название варианта упаковки товара не может быть больше 255 символов");
    }

    this.value = name;
    this.translations = translations;
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
    if (!(o instanceof ProductPackageOptionName productPackageOptionName)) {
      return false;
    }
    return Objects.equals(value, productPackageOptionName.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

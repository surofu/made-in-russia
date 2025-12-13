package com.surofu.exporteru.core.model.vendorDetails.productCategory;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
public final class VendorProductCategoryName implements Serializable {
  @Column(name = "name", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "name_translations")
  private final Map<String, String> translations;

  public VendorProductCategoryName(String name, Map<String, String> translations) {
    if (name == null || name.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.vendor.product_category.name.empty");
    }

    if (name.length() > 255) {
      throw new LocalizedValidationException("validation.vendor.product_category.name.max_length");
    }

    this.value = name;
    this.translations = translations;
  }

  public VendorProductCategoryName(String name) {
    this.value = name;
    this.translations = new HashMap<>();
  }

  public VendorProductCategoryName() {
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
    if (!(o instanceof VendorProductCategoryName that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

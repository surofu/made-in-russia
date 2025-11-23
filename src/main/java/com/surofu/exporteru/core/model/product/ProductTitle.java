package com.surofu.exporteru.core.model.product;

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
public final class ProductTitle implements Serializable {

  @Column(name = "title", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "title_translations")
  private Map<String, String> translations = new HashMap<>();

  private ProductTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Название товара не должно быть пустым");
    }

    if (title.length() > 255) {
      throw new IllegalArgumentException("Название товара не должно быть больше 255 символов");
    }

    this.value = title;
  }

  public static ProductTitle of(String title) {
    return new ProductTitle(title);
  }

  public String getLocalizedValue(Locale locale) {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
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
    if (!(o instanceof ProductTitle productTitle)) {
      return false;
    }
    return Objects.equals(value, productTitle.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}

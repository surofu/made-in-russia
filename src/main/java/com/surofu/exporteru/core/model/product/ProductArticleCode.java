package com.surofu.exporteru.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductArticleCode implements Serializable {
  @Transient
  private static final Pattern ARTICLE_CODE_PATTERN = Pattern.compile("^[A-Za-z]{4}-[0-9]{4}$");
  @Column(name = "article_code", nullable = false, unique = true, updatable = false)
  private String value;

  public ProductArticleCode(String code) {
    if (code == null || code.trim().isEmpty()) {
      throw new IllegalArgumentException("Артикул товара не может быть пустым");
    }
    if (!ARTICLE_CODE_PATTERN.matcher(code).matches()) {
      throw new IllegalArgumentException(
          "Артикул товара должен соответствовать выражению ^[A-Za-z]{4}-[0-9]{4}$");
    }
    this.value = code;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductArticleCode that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

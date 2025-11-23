package com.surofu.exporteru.core.model.product.review;

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
public final class ProductReviewContent implements Serializable {

  @Column(name = "content", nullable = false, columnDefinition = "text")
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "content_translations")
  private Map<String, String> translations = new HashMap<>();

  private ProductReviewContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.product.review.content.empty");
    }

    if (content.length() >= 10_000) {
      throw new LocalizedValidationException("validation.product.review.content.max_length");
    }

    this.value = content;
  }

  public String getLocalizedValue(Locale locale) {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }

    return translations.getOrDefault(locale.getLanguage(), Objects.requireNonNullElse(value, ""));
  }

  public static ProductReviewContent of(String content) {
    return new ProductReviewContent(content);
  }

  @Override
  public String toString() {
    return value;
  }
}

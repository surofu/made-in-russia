package com.surofu.exporteru.core.model.product.faq;

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
public final class ProductFaqQuestion implements Serializable {

  @Column(name = "question", nullable = false, columnDefinition = "text")
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "question_translations")
  private final Map<String, String> translations;

  public ProductFaqQuestion(String question, Map<String, String> translations) {
    if (question == null || question.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.faq.question.empty");
    }

    if (question.length() >= 20_000) {
      throw new LocalizedValidationException("validation.faq.question.max_length");
    }

    this.value = question;
    this.translations = translations;
  }

  public ProductFaqQuestion() {
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
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (!(o instanceof ProductFaqQuestion productFaqQuestion)) {
          return false;
      }
    return Objects.equals(value, productFaqQuestion.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

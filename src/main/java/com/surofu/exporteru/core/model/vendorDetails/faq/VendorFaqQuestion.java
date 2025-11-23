package com.surofu.exporteru.core.model.vendorDetails.faq;

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
public final class VendorFaqQuestion implements Serializable {

  @Column(name = "question", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "question_translations")
  private Map<String, String> translations = new HashMap<>();

  // TODO: Translate
  private VendorFaqQuestion(String question) {
    if (question == null || question.trim().isEmpty()) {
      throw new IllegalArgumentException("Вопрос не может быть пустым");
    }

    if (question.length() >= 20_000) {
      throw new IllegalArgumentException("Вопрос не может быть больше 20,000 символов");
    }

    this.value = question;
  }

  public static VendorFaqQuestion of(String question) {
    return new VendorFaqQuestion(question);
  }

  public String getLocalizedValue(Locale locale) {
    if (translations == null || translations.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    return translations.getOrDefault(locale.getLanguage(), value);
  }

  @Override
  public String toString() {
    return value;
  }
}

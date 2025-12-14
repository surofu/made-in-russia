package com.surofu.exporteru.core.model.faq;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FaqAnswer implements Serializable {
  @Column(name = "answer", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "answer_translations")
  private Map<String, String> translations;

  public FaqAnswer(String answer, Map<String, String> translations) {
    if (answer == null || answer.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.faq.answer.empty");
    }

    if (answer.length() > 20_000) {
      throw new LocalizedValidationException("validation.faq.answer.max_length");
    }

    this.value = answer;
    this.translations = translations != null
        ? new HashMap<>(translations)
        : new HashMap<>();
  }

  public Map<String, String> getTranslations() {
    return translations != null
        ? Collections.unmodifiableMap(translations)
        : Collections.emptyMap();
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof FaqAnswer faqAnswer)) {
      return false;
    }
    return Objects.equals(value, faqAnswer.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

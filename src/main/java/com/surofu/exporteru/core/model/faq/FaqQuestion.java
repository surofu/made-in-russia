package com.surofu.exporteru.core.model.faq;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
public final class FaqQuestion implements Serializable {

  @Column(name = "question", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "question_translations")
  private Map<String, String> translations = new HashMap<>();

  private FaqQuestion(String question) {
    if (question == null || question.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.faq.question.empty");
    }

    if (question.length() > 20_000) {
      throw new LocalizedValidationException("validation.faq.question.max_length");
    }

    this.value = question;
  }

  public static FaqQuestion of(String question) {
    return new FaqQuestion(question);
  }

  @Override
  public String toString() {
    return value;
  }
}

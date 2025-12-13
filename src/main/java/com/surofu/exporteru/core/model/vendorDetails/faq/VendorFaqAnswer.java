package com.surofu.exporteru.core.model.vendorDetails.faq;

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
public final class VendorFaqAnswer implements Serializable {

  @Column(name = "answer", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "answer_translations")
  private Map<String, String> translations = new HashMap<>();

  private VendorFaqAnswer(String answer) {
    if (answer == null || answer.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.faq.answer.empty");
    }

    if (answer.length() >= 20_000) {
      throw new LocalizedValidationException("validation.faq.answer.max_length");
    }

    this.value = answer;
  }

  public static VendorFaqAnswer of(String answer) {
    return new VendorFaqAnswer(answer);
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
}

package com.surofu.exporteru.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDescription implements Serializable {
  @Column(name = "main_description", nullable = false, columnDefinition = "text")
  private String mainDescription;
  @Column(name = "further_description", columnDefinition = "text")
  private String furtherDescription;
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "main_description_translations")
  private Map<String, String> mainDescriptionTranslations;
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "further_description_translations")
  private Map<String, String> furtherDescriptionTranslations;

  public ProductDescription(String mainDescription, String furtherDescription,
                            Map<String, String> mainDescriptionTranslations,
                            Map<String, String> furtherDescriptionTranslations) {
    validateMainDescription(mainDescription);
    validateFurtherDescription(furtherDescription);

    this.mainDescription = mainDescription;
    this.furtherDescription = furtherDescription;
    this.mainDescriptionTranslations = mainDescriptionTranslations != null
        ? new HashMap<>(mainDescriptionTranslations)
        : new HashMap<>();
    this.furtherDescriptionTranslations = furtherDescriptionTranslations != null
        ? new HashMap<>(furtherDescriptionTranslations)
        : new HashMap<>();
  }

  public String getLocalizedMainDescription() {
    if (mainDescriptionTranslations == null || mainDescriptionTranslations.isEmpty()) {
      return Objects.requireNonNullElse(mainDescription, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return mainDescriptionTranslations.getOrDefault(locale.getLanguage(),
        Objects.requireNonNullElse(mainDescription, ""));
  }

  public String getLocalizedFurtherDescription() {
    if (furtherDescription == null || furtherDescriptionTranslations.isEmpty()) {
      return Objects.requireNonNullElse(furtherDescription, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return furtherDescriptionTranslations.getOrDefault(locale.getLanguage(),
        furtherDescription);
  }

  public Map<String, String> getMainDescriptionTranslations() {
    return mainDescriptionTranslations != null
        ? Collections.unmodifiableMap(mainDescriptionTranslations)
        : Collections.emptyMap();
  }

  public Map<String, String> getFurtherDescriptionTranslations() {
    return furtherDescriptionTranslations != null
        ? Collections.unmodifiableMap(furtherDescriptionTranslations)
        : Collections.emptyMap();
  }

  private void validateMainDescription(String mainDescription) {
    if (mainDescription == null || mainDescription.trim().isEmpty()) {
      throw new IllegalArgumentException("Главное описание не может быть пустым");
    }
    if (mainDescription.length() >= 50_000) {
      throw new IllegalArgumentException("Главное описание не может быть больше 50,000 символов");
    }
  }

  private void validateFurtherDescription(String furtherDescription) {
    if (furtherDescription != null && furtherDescription.length() >= 20_000) {
      throw new IllegalArgumentException(
          "Второстепенное описание не может быть больше 20,000 символов");
    }
  }
}
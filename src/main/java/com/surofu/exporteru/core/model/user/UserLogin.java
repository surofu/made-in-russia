package com.surofu.exporteru.core.model.user;

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
public final class UserLogin implements Serializable {

  @Column(name = "login", nullable = false)
  private final String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "login_transliteration")
  private final Map<String, String> transliteration;

  public UserLogin(String login, Map<String, String> transliteration) {
    if (login == null || login.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.login.empty");
    }

    if (login.length() < 2) {
      throw new LocalizedValidationException("validation.login.min_length");
    }

    if (login.length() > 255) {
      throw new LocalizedValidationException("validation.login.max_length");
    }

    this.value = login;
    this.transliteration = transliteration;
  }

  public UserLogin(String login) {
    this(login, new HashMap<>());
  }

  public UserLogin() {
    this.value = null;
    this.transliteration = new HashMap<>();
  }

  public String getLocalizedValue() {
    if (transliteration == null || transliteration.isEmpty()) {
      return Objects.requireNonNullElse(value, "");
    }
    Locale locale = LocaleContextHolder.getLocale();
    return transliteration.getOrDefault(locale.getLanguage(),
        Objects.requireNonNullElse(value, ""));
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserLogin userLogin)) {
      return false;
    }
    return Objects.equals(value, userLogin.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

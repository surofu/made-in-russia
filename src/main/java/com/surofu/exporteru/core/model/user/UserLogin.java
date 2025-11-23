package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
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
public final class UserLogin implements Serializable {

  @Column(name = "login", nullable = false)
  private String value;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "login_transliteration")
  private Map<String, String> transliteration = new HashMap<>();

  private UserLogin(String login) {
    this.value = login;
  }

  public static UserLogin of(String login) {
    if (login == null || login.trim().isEmpty()) {
      throw new LocalizedValidationException("validation.login.empty");
    }

    if (login.length() < 2) {
      throw new LocalizedValidationException("validation.login.min_length");
    }

    if (login.length() > 255) {
      throw new LocalizedValidationException("validation.login.max_length");
    }

    return new UserLogin(login);
  }

  public String getLocalizedValue(Locale locale) {
    if (transliteration == null || transliteration.isEmpty()) {
      return value;
    }

    return transliteration.getOrDefault(locale.getLanguage(), value);
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
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    return value != null && value.equals(((UserLogin) o).value);
  }
}

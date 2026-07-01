package com.surofu.exporteru.application.components;

import com.ibm.icu.text.Transliterator;
import com.surofu.exporteru.core.model.user.UserLogin;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransliterationManager {

  private final Transliterator transliteratorLatin;

  public String transliterate(@NonNull String text) {
    if (StringUtils.trimToNull(text) == null) {
      return "";
    }
    return transliteratorLatin.transliterate(text);
  }

  public UserLogin transliterateUserLogin(UserLogin userLogin, Locale locale) {
    String rawLogin = userLogin.toString();
    String transliteratedLogin = transliteratorLatin.transliterate(rawLogin);
    Map<String, String> transliterationMap = switch (locale.getLanguage()) {
      case "ru" -> Map.of(
          "en", transliteratedLogin,
          "ru", rawLogin,
          "zh", transliteratedLogin,
          "hi", transliteratedLogin
      );
      case "zh" -> Map.of(
          "en", transliteratedLogin,
          "ru", transliteratedLogin,
          "zh", rawLogin,
          "hi", transliteratedLogin
      );
      case "hi" -> Map.of(
          "en", transliteratedLogin,
          "ru", transliteratedLogin,
          "zh", transliteratedLogin,
          "hi", rawLogin
      );
      default -> Map.of(
          "en", transliteratedLogin,
          "ru", transliteratedLogin,
          "zh", transliteratedLogin,
          "hi", transliteratedLogin
      );
    };
    return new UserLogin(userLogin.getValue(), transliterationMap);
  }
}

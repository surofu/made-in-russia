package com.surofu.exporteru.application.components;

import com.surofu.exporteru.core.model.user.UserLogin;
import java.util.Locale;
import java.util.Map;
import me.saitov.libs.transliteration.Transliteration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TransliterationManager {
    public static String transliterate(@NonNull final String text) {
        if (StringUtils.trimToNull(text) == null) {
            return "";
        }
        return Transliteration.transliterate(text, ' ');
    }

    public static UserLogin transliterateUserLogin(UserLogin userLogin, Locale locale) {
        String rawLogin = userLogin.toString();
        String transliteratedLogin = TransliterationManager.transliterate(rawLogin);
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

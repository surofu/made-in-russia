package com.surofu.exporteru.application.components;

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
}

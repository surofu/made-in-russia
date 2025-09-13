package com.surofu.madeinrussia.application.components;

import me.saitov.libs.transliteration.Transliteration;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TransliterationManager {
    public static String transliterate(@NonNull final String text) {
        return Transliteration.transliterate(text, ' ');
    }
}

package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.infrastructure.config.LocalizationConfig;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Configuration
public class DefaultLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String language = request.getHeader("Accept-Language");

        if (language == null || language.isEmpty()) {
            return Locale.getDefault();
        }

        Locale locale = Locale.forLanguageTag(language);

        if (LocalizationConfig.LOCALES.contains(locale)) {
            return locale;
        }

        return Locale.getDefault();
    }

    @Override
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {

    }
}

package com.surofu.madeinrussia.application.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LocalizationManager {

    private final MessageSource messageSource;

    public String localize(String messageCode, Locale locale, Object ...args) {
        String messageTemplate = messageSource.getMessage(messageCode, null, locale);
        return String.format(messageTemplate, args);
    }

    public String localize(String messageCode, Object ...args) {
        Locale locale = LocaleContextHolder.getLocale();
        return localize(messageCode, locale, args);
    }
}

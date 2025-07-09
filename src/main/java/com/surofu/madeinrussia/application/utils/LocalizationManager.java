package com.surofu.madeinrussia.application.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LocalizationManager {

    private final MessageSource messageSource;

    private final LocaleResolver localeResolver;

    public String localize(String messageCode, Object ...args) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String messageTemplate = messageSource.getMessage(messageCode, null, Locale.getDefault());

        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            messageTemplate = messageSource.getMessage(messageCode, null, localeResolver.resolveLocale(request));
        }

        return String.format(messageTemplate, args);
    }
}

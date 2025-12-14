package com.surofu.exporteru.infrastructure.config;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class LocalizationConfig {
    public static final List<Locale> LOCALES = Arrays.asList(
            new Locale("en"),
            new Locale("ru"),
            new Locale("zh")
    );
}

package com.surofu.exporteru.infrastructure.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalizationConfig {
  public static final List<Locale> LOCALES = Arrays.asList(
      Locale.of("en"),
      Locale.of("ru"),
      Locale.of("zh")
  );
}

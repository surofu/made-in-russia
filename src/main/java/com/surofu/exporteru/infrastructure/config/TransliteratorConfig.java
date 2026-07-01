package com.surofu.exporteru.infrastructure.config;

import com.ibm.icu.text.Transliterator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransliteratorConfig {

  @Bean
  public Transliterator transliteratorLatin() {
    return Transliterator.getInstance("Any-Latin; Latin-ASCII");
  }
}

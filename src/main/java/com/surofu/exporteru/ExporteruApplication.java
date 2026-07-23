package com.surofu.exporteru;

import com.surofu.exporteru.infrastructure.config.TelegramBotRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class ExporteruApplication implements ApplicationRunner {

  private final TelegramBotRegistrar botRegistrar;

  @Value("${telegram.bot.russian.enable:false}")
  private boolean botEnableRussian;
  @Value("${telegram.bot.english.enable:false}")
  private boolean botEnableEnglish;
  @Value("${telegram.bot.china.enable:false}")
  private boolean botEnableChina;

  public ExporteruApplication(
      TelegramBotRegistrar botRegistrar
  ) {
    this.botRegistrar = botRegistrar;
  }

  public static void main(String[] args) {
    SpringApplication.run(ExporteruApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    if (botEnableRussian) {
      botRegistrar.registerRussianBot();
    }
    if (botEnableEnglish) {
      botRegistrar.registerEnglishBot();
    }
    if (botEnableChina) {
      botRegistrar.registerChinaBot();
    }
  }
}
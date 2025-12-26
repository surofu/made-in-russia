package com.surofu.exporteru;

import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLinkAccountHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotLoginHandler;
import com.surofu.exporteru.application.components.telegrambot.TelegramBotRegisterHandler;
import com.surofu.exporteru.infrastructure.config.telegrambot.TelegramBotHandlerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class ExporteruApplication implements ApplicationRunner {
  private final TelegramBotHandlerConfig telegramBotHandlerConfig;
  private final TelegramBot telegramBotRussian;
  private final TelegramBot telegramBotEnglish;
  private final TelegramBot telegramBotChina;
  @Value("${telegram.bot.russian.enable:false}")
  private boolean botEnableRussian;
  @Value("${telegram.bot.english.enable:false}")
  private boolean botEnableEnglish;
  @Value("${telegram.bot.china.enable:false}")
  private boolean botEnableChina;

  public ExporteruApplication(
      @Qualifier("telegramBot")
      TelegramBot telegramBotRussian,
      @Qualifier("englishTelegramBot")
      TelegramBot telegramBotEnglish,
      @Qualifier("chinaTelegramBot")
      TelegramBot telegramBotChina,
      TelegramBotHandlerConfig telegramBotHandlerConfig,
      TelegramBotRegisterHandler registerHandler,
      TelegramBotLoginHandler loginHandler,
      TelegramBotLinkAccountHandler linkAccountHandler
  ) {
    this.telegramBotRussian = telegramBotRussian;
    this.telegramBotEnglish = telegramBotEnglish;
    this.telegramBotChina = telegramBotChina;
    this.telegramBotHandlerConfig = telegramBotHandlerConfig;
    telegramBotHandlerConfig.initializeTelegramBots(telegramBotRussian, telegramBotEnglish,
        telegramBotChina, registerHandler, loginHandler, linkAccountHandler);
  }

  public static void main(String[] args) {
    SpringApplication.run(ExporteruApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws TelegramApiException {
    if (botEnableRussian) {
      runTelegramBotRussian();
    }
    if (botEnableEnglish) {
      runTelegramBotEnglish();
    }
    if (botEnableChina) {
      runTelegramBotChina();
    }
  }

  private void runTelegramBotRussian() throws TelegramApiException {
    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
    api.registerBot(telegramBotRussian);
  }

  private void runTelegramBotEnglish() throws TelegramApiException {
    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
    api.registerBot(telegramBotEnglish);
  }

  private void runTelegramBotChina() throws TelegramApiException {
    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
    api.registerBot(telegramBotChina);
  }
}
package com.surofu.exporteru.infrastructure.config;

import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Service
public class TelegramBotRegistrar {
  private final TelegramBot russianBot;
  private final TelegramBot englishBot;
  private final TelegramBot chinaBot;
  private final RetryTemplate retryTemplate;

  public TelegramBotRegistrar(
      @Qualifier("telegramBot") TelegramBot russianBot,
      @Qualifier("englishTelegramBot") TelegramBot englishBot,
      @Qualifier("chinaTelegramBot") TelegramBot chinaBot) {
    this.russianBot = russianBot;
    this.englishBot = englishBot;
    this.chinaBot = chinaBot;
    this.retryTemplate = RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .build();
  }

  @Async
  public void registerRussianBot() {
    registerBot(russianBot, "Russian");
  }

  @Async
  public void registerEnglishBot() {
    registerBot(englishBot, "English");
  }

  @Async
  public void registerChinaBot() {
    registerBot(chinaBot, "China");
  }

  private void registerBot(TelegramBot bot, String name) {
    retryTemplate.execute(context -> {
      try {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        log.info("{} bot registered successfully", name);
        return null;
      } catch (TelegramApiException e) {
        log.warn("Failed to register {} bot, attempt {}", name, context.getRetryCount() + 1);
        throw new RuntimeException(e); // retry
      }
    });
  }
}

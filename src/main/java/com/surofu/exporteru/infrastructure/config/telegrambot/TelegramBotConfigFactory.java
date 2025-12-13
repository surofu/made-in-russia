package com.surofu.exporteru.infrastructure.config.telegrambot;

import com.surofu.exporteru.application.components.telegrambot.KeyboardBuilder;
import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
public class TelegramBotConfigFactory {

  private final UserRepository userRepository;
  private final KeyboardBuilder keyboardBuilder;
  private final LocalizationManager localizationManager;

  public TelegramBotConfigFactory(
      UserRepository userRepository,
      KeyboardBuilder keyboardBuilder,
      LocalizationManager localizationManager) {
    this.userRepository = userRepository;
    this.keyboardBuilder = keyboardBuilder;
    this.localizationManager = localizationManager;
  }

  @Bean
  public TelegramLongPollingBot russianTelegramLongPollingBot(
      @Qualifier("russianTelegramBotConfig") RussianTelegramBotConfig config) {
    return new TelegramBot(config, userRepository, keyboardBuilder, localizationManager);
  }

  @Bean
  public TelegramLongPollingBot englishTelegramLongPollingBot(
      @Qualifier("englishTelegramBotConfig") EnglishTelegramBotConfig config) {
    return new TelegramBot(config, userRepository, keyboardBuilder, localizationManager);
  }

  @Bean
  public TelegramLongPollingBot chinaTelegramLongPollingBot(
      @Qualifier("chinaTelegramBotConfig") ChinaTelegramBotConfig config) {
    return new TelegramBot(config, userRepository, keyboardBuilder, localizationManager);
  }

  @Primary
  @Bean(name = "russianTelegramBot")
  public TelegramBot russianTelegramBot(
      @Qualifier("russianTelegramLongPollingBot") TelegramLongPollingBot bot) {
    return (TelegramBot) bot;
  }

  @Bean(name = "englishTelegramBot")
  public TelegramBot englishTelegramBot(
      @Qualifier("englishTelegramLongPollingBot") TelegramLongPollingBot bot) {
    return (TelegramBot) bot;
  }

  @Bean(name = "chinaTelegramBot")
  public TelegramBot chinaTelegramBot(
      @Qualifier("chinaTelegramLongPollingBot") TelegramLongPollingBot bot) {
    return (TelegramBot) bot;
  }
}

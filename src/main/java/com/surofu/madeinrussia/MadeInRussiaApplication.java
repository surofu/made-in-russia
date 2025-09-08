package com.surofu.madeinrussia;

import com.surofu.madeinrussia.application.components.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@EnableAsync
@EnableCaching
@SpringBootApplication
@RequiredArgsConstructor
public class MadeInRussiaApplication implements ApplicationRunner {

    @Value("${telegram.bot.enable:false}")
    private boolean bootEnabled;

    private final TelegramBot bot;

    public static void main(String[] args) {
        SpringApplication.run(MadeInRussiaApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws TelegramApiException {
        if (bootEnabled) {
            runTelegramBot();
        }
    }

    private void runTelegramBot() throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
    }
}
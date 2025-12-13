package com.surofu.exporteru;

import com.surofu.exporteru.application.components.telegrambot.TelegramBot;
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

    @Value("${telegram.bot.enable:false}")
    private boolean botEnabled;

    @Value("${telegram.bot2.enable:false}")
    private boolean bot2Enabled;

    private final TelegramBot telegramBot;
    private final TelegramBot telegramBot2;

    public ExporteruApplication(
            @Qualifier("telegramBot")
            TelegramBot telegramBot,
            @Qualifier("englishTelegramBot")
            TelegramBot telegramBot2
    ) {
        this.telegramBot = telegramBot;
        this.telegramBot2 = telegramBot2;
    }

    public static void main(String[] args) {
        SpringApplication.run(ExporteruApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws TelegramApiException {
        if (botEnabled) {
            runTelegramBot();
        }

        if (bot2Enabled) {
            runTelegramBot2();
        }
    }

    private void runTelegramBot() throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot);
    }

    private void runTelegramBot2() throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot2);
    }
}
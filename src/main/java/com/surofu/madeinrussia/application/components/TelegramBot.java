package com.surofu.madeinrussia.application.components;

import com.surofu.madeinrussia.infrastructure.config.TelegramBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfig config;

    public TelegramBot(TelegramBotConfig config) {
        super(config.getBotToken());

        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info("Message: {}", update.getMessage().getText());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
            log.info("Sent message to chat {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chat {}", chatId, e);
        }
    }
}

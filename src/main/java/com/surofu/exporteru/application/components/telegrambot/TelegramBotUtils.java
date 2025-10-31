package com.surofu.exporteru.application.components.telegrambot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramBotUtils {
    public static long safeGetChatId(Update update) throws IllegalArgumentException {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        throw new IllegalArgumentException("Cannot get chat id");
    }

    public static User safeGetUser(Update update) throws IllegalArgumentException {
        if (update.hasMessage()) {
            return update.getMessage().getFrom();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        }

        throw new IllegalArgumentException("Cannot get from");
    }

    public static String safeGetText(Update update) throws IllegalArgumentException {
        if (update.hasMessage()) {
            return update.getMessage().getText();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }

        throw new IllegalArgumentException("Cannot get text");
    }

    public static Message safeGetMessage(Update update) throws IllegalArgumentException {
        if (update.hasMessage()) {
            return update.getMessage();
        }
        throw new IllegalArgumentException("Cannot get message");
    }

    public static String saveGetCallbackId(Update update) throws IllegalArgumentException {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getId();
        }
        throw new IllegalArgumentException("Cannot get callback id");
    }
}

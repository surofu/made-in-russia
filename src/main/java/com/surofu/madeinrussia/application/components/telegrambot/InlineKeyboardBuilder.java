package com.surofu.madeinrussia.application.components.telegrambot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardBuilder {
    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> row = null;

    public static InlineKeyboardBuilder create() {
        return new InlineKeyboardBuilder();
    }

    public InlineKeyboardBuilder row() {
        if (row != null) {
            keyboard.add(row);
        }
        row = new ArrayList<>();
        return this;
    }

    public InlineKeyboardBuilder button(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        row.add(button);
        return this;
    }

    public InlineKeyboardBuilder urlButton(String text, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        row.add(button);
        return this;
    }

    public InlineKeyboardMarkup build() {
        if (row != null && !row.isEmpty()) {
            keyboard.add(row);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}

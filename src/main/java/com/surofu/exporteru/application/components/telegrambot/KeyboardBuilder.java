package com.surofu.exporteru.application.components.telegrambot;

import com.surofu.exporteru.application.utils.LocalizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeyboardBuilder {
    private final LocalizationManager localizationManager;

    public ReplyKeyboardMarkup createMainMenu() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();

        // Первый ряд
        KeyboardRow row1 = new KeyboardRow();
        String text = localizationManager.localize("telegram.bot.keyboard.cancel");
        row1.add(text);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("English");
        row2.add("Русский");
        row2.add("中文");

        rows.add(row1);
        rows.add(row2);

        keyboard.setKeyboard(rows);
        keyboard.setIsPersistent(true);
        return keyboard;
    }
}
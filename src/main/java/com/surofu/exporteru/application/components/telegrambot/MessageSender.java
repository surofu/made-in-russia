package com.surofu.exporteru.application.components.telegrambot;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface MessageSender {
    Message sendMessage(Long chatId, String text);
    Message sendMessage(Long chatId, String text, ReplyKeyboard markup);
    void answerCallback(String callbackQueryId, String text, boolean showAlert);
    void editMessageText(EditMessageText editMessageText) throws TelegramApiException;
    void deleteMessage(DeleteMessage deleteMessage) throws TelegramApiException;
    void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) throws TelegramApiException;
}

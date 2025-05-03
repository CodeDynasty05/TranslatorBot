package com.nazim;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyTelegramBot extends TelegramLongPollingBot {
    private final String BOT_USERNAME = System.getenv("TELEGRAM_BOT_USERNAME");
    private final String BOT_TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");
    private final BotCommands commands = new BotCommands();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = String.valueOf(update.getMessage().getChatId());
            String username = update.getMessage().getFrom().getUserName();
            if (messageText.startsWith("/start")) {
                executeMessage(commands.startCommand(chatId, username));
            } else if (messageText.startsWith("/setlang")) {
                executeMessage(commands.setLang(chatId));
            } else if (messageText.startsWith("/translate")) {
                if (update.getMessage().isReply()) {
                    Message repliedToMessage = update.getMessage().getReplyToMessage();
                    executeMessage(commands.translateReply(chatId, repliedToMessage, username));
                } else {
                    executeMessage(commands.translateDirect(chatId, messageText, username));
                }
            } else if (messageText.startsWith("/add")) {
                executeMessage(commands.addCommand(chatId));
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
            Integer messageId = callbackQuery.getMessage().getMessageId();
            String username = callbackQuery.getFrom().getUserName();
            executeMessage(commands.setLanguageCallback(chatId, callbackData, messageId, username));
        }
    }

    private <T extends BotApiMethod> void executeMessage(T message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

}
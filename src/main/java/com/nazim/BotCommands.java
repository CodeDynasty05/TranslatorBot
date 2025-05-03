package com.nazim;

import com.darkprograms.speech.translator.GoogleTranslate;
import org.bson.Document;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.*;

public class BotCommands {
    private final UserService userService = new UserService();
    private final List<LanguageOption> languages = Arrays.asList(
            new LanguageOption("Amharic", "am"),
            new LanguageOption("Arabic", "ar"),
            new LanguageOption("Basque", "eu"),
            new LanguageOption("Bengali", "bn"),
            new LanguageOption("English (UK)", "en-GB"),
            new LanguageOption("Portuguese (Brazil)", "pt-BR"),
            new LanguageOption("Bulgarian", "bg"),
            new LanguageOption("Catalan", "ca"),
            new LanguageOption("Cherokee", "chr"),
            new LanguageOption("Croatian", "hr"),
            new LanguageOption("Czech", "cs"),
            new LanguageOption("Danish", "da"),
            new LanguageOption("Dutch", "nl"),
            new LanguageOption("English (US)", "en"),
            new LanguageOption("Estonian", "et"),
            new LanguageOption("Filipino", "fil"),
            new LanguageOption("Finnish", "fi"),
            new LanguageOption("French", "fr"),
            new LanguageOption("German", "de"),
            new LanguageOption("Greek", "el"),
            new LanguageOption("Gujarati", "gu"),
            new LanguageOption("Hebrew", "iw"),
            new LanguageOption("Hindi", "hi"),
            new LanguageOption("Hungarian", "hu"),
            new LanguageOption("Icelandic", "is"),
            new LanguageOption("Indonesian", "id"),
            new LanguageOption("Italian", "it"),
            new LanguageOption("Japanese", "ja"),
            new LanguageOption("Kannada", "kn"),
            new LanguageOption("Korean", "ko"),
            new LanguageOption("Latvian", "lv"),
            new LanguageOption("Lithuanian", "lt"),
            new LanguageOption("Malay", "ms"),
            new LanguageOption("Malayalam", "ml"),
            new LanguageOption("Marathi", "mr"),
            new LanguageOption("Norwegian", "no"),
            new LanguageOption("Polish", "pl"),
            new LanguageOption("Portuguese (Portugal)", "pt-PT"),
            new LanguageOption("Romanian", "ro"),
            new LanguageOption("Russian", "ru"),
            new LanguageOption("Serbian", "sr"),
            new LanguageOption("Chinese (PRC)", "zh-CN"),
            new LanguageOption("Slovak", "sk"),
            new LanguageOption("Slovenian", "sl"),
            new LanguageOption("Spanish", "es"),
            new LanguageOption("Swahili", "sw"),
            new LanguageOption("Swedish", "sv"),
            new LanguageOption("Tamil", "ta"),
            new LanguageOption("Telugu", "te"),
            new LanguageOption("Thai", "th"),
            new LanguageOption("Chinese (Taiwan)", "zh-TW"),
            new LanguageOption("Turkish", "tr"),
            new LanguageOption("Urdu", "ur"),
            new LanguageOption("Ukrainian", "uk"),
            new LanguageOption("Vietnamese", "vi"),
            new LanguageOption("Welsh", "cy")
    );
    Map<Integer, String> userLanguageCache = new HashMap<>();

    public SendMessage startCommand(String chatId, String username) {
        int userId = generateUserIdFromUsername(username);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Welcome to the Translator Bot.\n /setlang to set language")
                .build();
        return message;
    }

    public SendMessage setLang(String chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Select language to translate:")
                .build();

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(createLanguageKeyboard());
        message.setReplyMarkup(markupInline);
        return message;
    }

    public SendMessage unknownCommand(String chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("I don't know what to do with that command.")
                .build();

        return message;
    }

    public EditMessageText setLanguageCallback(String chatId, String callbackData, Integer messageId, String username) {
        String language = languages.stream().filter(l -> l.code.equals(callbackData)).findFirst().get().name;
        int userId = generateUserIdFromUsername(username);
        EditMessageText message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("Language set to " + language)
                .build();
        userService.createOrUpdateUser(userId, username, callbackData);
        userLanguageCache.put(userId, callbackData);
        return message;
    }

    public SendMessage translateDirect(String chatId, String text, String username) {
        String language = getUserLanguage(generateUserIdFromUsername(username));
        String textToTranslate = text.substring(text.indexOf(" ") + 1);
        String translatedText;
        try {
            // Remove emojis before translation
            String cleanText = textToTranslate.replaceAll("[^\\p{L}\\p{N}\\p{P}\\s]", "");
            translatedText = GoogleTranslate.translate(language, cleanText);
            // Replace escaped newlines with actual newlines
            translatedText = translatedText.replace("\\n", "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(translatedText)
                .parseMode("HTML")  // Enable HTML parsing
                .build();

        return message;
    }

    public SendMessage translateReply(String chatId, Message message, String username) {
        String language = getUserLanguage(generateUserIdFromUsername(username));
        String textToTranslate = message.getText();
        String translatedText;
        try {
            // Remove emojis before translation
            String cleanText = textToTranslate.replaceAll("[^\\p{L}\\p{N}\\p{P}\\s]", "");
            translatedText = GoogleTranslate.translate(language, cleanText);
            // Replace escaped newlines with actual newlines
            translatedText = translatedText.replace("\\n", "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return SendMessage.builder()
                .chatId(chatId)
                .text(translatedText)
                .parseMode("HTML")  // Enable HTML parsing
                .replyToMessageId(message.getMessageId())
                .build();
    }

    public SendMessage addCommand(String chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Select group to add this bot:")
                .build();

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(createAddGroupKeyboard());
        message.setReplyMarkup(markupInline);
        return message;
    }

    public String getUserLanguage(int userId) {
        if (userLanguageCache.containsKey(userId)) {
            return userLanguageCache.get(userId);
        }

        Document user = userService.getUserById(userId);
        if (user != null) {
            String language = user.getString("language");
            userLanguageCache.put(userId, language);
            return language;
        }

        // Default fallback language
        return "en";
    }

    public List<List<InlineKeyboardButton>> createLanguageKeyboard() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Create rows with 2 buttons each
        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        for (LanguageOption lang : languages) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(lang.name);
            button.setCallbackData(lang.code);

            currentRow.add(button);

            if (currentRow.size() == 2) {
                rowsInline.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }

        // Add the last row if it has any remaining buttons
        if (!currentRow.isEmpty()) {
            rowsInline.add(currentRow);
        }

        return rowsInline;
    }

    public List<List<InlineKeyboardButton>> createAddGroupKeyboard() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Add group");
        button.setUrl("https://t.me/translatorNG_bot?startgroup=a");
        currentRow.add(button);
        rowsInline.add(currentRow);
        return rowsInline;
    }

    public int generateUserIdFromUsername(String username) {
        return Math.abs(username.hashCode());
    }
}
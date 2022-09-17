package org.bot.investmentBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

public class AdminPanel {

    public static void newUser(Update update, TelegramBot bot){
        SendMessage message = new SendMessage(1178010927, String.format("Новый пользователь: \nID: %d", update.message().chat().id()));
        InlineKeyboardButton banUser = new InlineKeyboardButton("Бан").callbackData("BAN_USER");
        InlineKeyboardButton allInfo = new InlineKeyboardButton("Полная информация").callbackData("USER_INFO");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(banUser);
        markup.addRow(allInfo);
        message.replyMarkup(markup);
        bot.execute(message);
    }

    public static void sendMessageForAdmin(String text, TelegramBot bot){
        SendMessage sendMessage = new SendMessage(1178010927, text);
        KeyboardButton botInfo = new KeyboardButton("Информация о боте");
        KeyboardButton transaction = new KeyboardButton("Управление транзакциями");
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(botInfo, transaction).resizeKeyboard(true);
        sendMessage.replyMarkup(markup);
        bot.execute(sendMessage);
    }
}

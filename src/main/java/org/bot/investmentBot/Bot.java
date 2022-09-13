package org.bot.investmentBot;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;


public class Bot {

    float deposit = 500000;
    float savings = 100;
    float balance = 0;
    int partners = 0;

    Time remainingTime = new Time(1, 5, 6);

    File file = new File("LOG.txt");

    static TelegramBot bot = new TelegramBot("5680309518:AAELs3qdKsAB5vlEaYjtnDIpNeYryZeRFXw");


    static String buttons[] = {"\uD83D\uDDA5 Инвестиции", "\uD83D\uDCB3 Кошелёк", "⚙️ Настройки", "\uD83D\uDC54 Партнёрам", "\uD83D\uDCE0 Калькулятор", "\uD83D\uDDD3 Обучение"};

    public Bot() {
        System.err.println("Bot Started!");
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::rss);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void rss(Update update) {
        String investText = MessageCreator.getInvestText(deposit, savings, remainingTime);

        System.err.println("Update is working!");

        if (update.message() != null) {
            System.err.println("MessageData");
            if (!update.message().text().isEmpty()) {

                String text = update.message().text();

                if (text.equals("/start")) {
                    sendStartMessage(update);
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDDA5 Инвестиции")) {
                    sendInvestmentMessage(update, investText);
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDCB3 Кошелёк")) {
                    sendWalletMessage(update);
                } else if (text.equals("⚙️ Настройки")) {
                    // TODO Реализовать функционал настроек
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDC54 Партнёрам")) {
                    // TODO Реализовать функционал партнерам
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDCE0 Калькулятор")) {
                    // TODO Реализовать функционал калькулятора
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDDD3 Обучение")) {
                    // TODO Реализовать функционал обучения
                    System.err.println("Sending Message");
                }
            }
        } else if (update.callbackQuery() != null) {
            System.err.println("CallBackData");
            if (update.callbackQuery().data().equals("INVEST")) {
                callbackForInvest(update);
            } else if (update.callbackQuery().data().equals("FLUSH")) {
               callbackForFlush(update);
            }
            logUpdate(update);
        }
    }

    void sendStartMessage(Update update) {
        System.err.println(update);
        sendMessage(update.message().chat().id(), "©️Главное меню");
    }
    void sendInvestmentMessage(Update update, String investText) {
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), investText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton investButton = new InlineKeyboardButton("➕").callbackData("INVEST");
        InlineKeyboardButton flushButton = new InlineKeyboardButton("➖").callbackData("FLUSH");
        keyboardMarkup.addRow(investButton, flushButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }
    void sendWalletMessage(Update update){
        String messageText = MessageCreator.getWalletText(update, remainingTime, balance, partners);
        sendMessage(update.message().chat().id(), messageText);
    }
    private void sendMessage(long chatID, String text) {
        SendMessage sendMessage = new SendMessage(chatID, text);
        ReplyKeyboardMarkup replyKeyboardMarkup = createMainKeyboard();
        sendMessage.replyMarkup(replyKeyboardMarkup);
        bot.execute(sendMessage);
    }

    void callbackForInvest(Update update){
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
        answerCallbackQuery.text(MessageCreator.getInvestCallback());
        answerCallbackQuery.showAlert(true);
        bot.execute(answerCallbackQuery);
    }
    void callbackForFlush(Update update){
        if (savings <= 0) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
            answerCallbackQuery.text(MessageCreator.getFlushCallback());
            answerCallbackQuery.showAlert(true);
            bot.execute(answerCallbackQuery);
        } else {
            balance += savings;
            savings = 0;
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
            answerCallbackQuery.text("✅Накопления успешно собраны!");
            answerCallbackQuery.showAlert(true);
            bot.execute(answerCallbackQuery);
            DeleteMessage deleteMessage = new DeleteMessage(update.callbackQuery().from().id(), update.callbackQuery().message().messageId());
            bot.execute(deleteMessage);
            System.out.println("Message Deleted!");
        }
    }
    void logUpdate(Update update){
        try {
            String updateS = update.toString();
            FileWriter writer = new FileWriter(file);
            writer.write(updateS.replaceAll(",", "\n"));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static ReplyKeyboardMarkup createMainKeyboard() {
        KeyboardButton button0 = new KeyboardButton(buttons[0]);
        KeyboardButton button1 = new KeyboardButton(buttons[1]);
        KeyboardButton button2 = new KeyboardButton(buttons[2]);
        KeyboardButton button3 = new KeyboardButton(buttons[3]);
        KeyboardButton button4 = new KeyboardButton(buttons[4]);
        KeyboardButton button5 = new KeyboardButton(buttons[5]);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(button0, button1);
        replyKeyboardMarkup.addRow(button2, button3);
        replyKeyboardMarkup.addRow(button4, button5);
        replyKeyboardMarkup.resizeKeyboard(true);

        return replyKeyboardMarkup;
    }
}

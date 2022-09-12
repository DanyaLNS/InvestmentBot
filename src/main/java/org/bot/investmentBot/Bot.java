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

    float vklad = 500000;
    float nakoplenie = 100;
    float balance = 0;
    int partners = 0;
    Time remainingTime = new Time(1, 5, 6);

    File file = new File("LOG.txt");

    TelegramBot bot = new TelegramBot("5663048702:AAEwHRRut1Nib4nuEK3yDizZVj9c3QC8v28");


    String buttons[] = {"\uD83D\uDDA5 Инвестиции", "\uD83D\uDCB3 Кошелёк", "⚙️ Настройки", "\uD83D\uDC54 Партнёрам", "\uD83D\uDCE0 Калькулятор", "\uD83D\uDDD3 Обучение"};

    public Bot() {

        System.err.println("Bot Started!");
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::rss);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void rss(Update update) {
         String investText = "▪️ Открывайте свой вклад ниже, а после получайте прибыль с него и собирайте ее в данном разделе:\n" +
                "\n" +
                "\uD83D\uDCE0 Процент от вклада: 4.2%\n" +
                "⏱ Время доходности: 24 часа\n" +
                "\uD83D\uDCC6 Срок вклада: Пожизненно\n" +
                "\n" + String.format("\uD83D\uDCB3 Ваш вклад: %.2f₽\n", vklad) +
                String.format("\uD83D\uDCB5 Накопление: %.2f₽\n", nakoplenie) +
                "\n" +
                "\uD83E\uDDED Время до сбора средств: " + remainingTime;

        System.err.println("Update is working!");


        if (update.message() != null) {
            System.err.println("MessageData");
            if (!update.message().text().isEmpty()) {

                String text = update.message().text();

                if (text.equals("/start")) {
                    System.err.println(update);
                    sendMessage(update.message().chat().id(), "©️Главное меню");
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDDA5 Инвестиции")) {
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(), investText);
                    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
                    InlineKeyboardButton investButton = new InlineKeyboardButton("➕").callbackData("INVEST");
                    InlineKeyboardButton flushButton = new InlineKeyboardButton("➖").callbackData("FLUSH");
                    keyboardMarkup.addRow(investButton, flushButton);
                    sendMessage.replyMarkup(keyboardMarkup);
                    bot.execute(sendMessage);
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDCB3 Кошелёк")) {
                    sendMessage(update.message().chat().id(), String.format(
                            "\uD83E\uDD16 Ваш ID: %d\n" +
                            "\uD83D\uDCC6 Профиль создан: %s\n" +
                            "\uD83D\uDCB3 Ваш баланс: %.2f₽\n" +
                            "\uD83D\uDC65 Партнеров: %d чел.", update.message().from().id(), remainingTime, balance, partners));
                } else if (text.equals("⚙️ Настройки")) {

                } else if (text.equals("\uD83D\uDC54 Партнёрам")) {

                } else if (text.equals("\uD83D\uDCE0 Калькулятор")) {

                } else if (text.equals("\uD83D\uDDD3 Обучение")) {

                }


            }
        } else if (update.callbackQuery() != null) {
            System.err.println("CallBackData");
            if (update.callbackQuery().data().equals("INVEST")) {
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
                answerCallbackQuery.text("\uD83D\uDEABПополните баланс, минимальная сумма для инвестиции: 100.0₽");
                answerCallbackQuery.showAlert(true);
                bot.execute(answerCallbackQuery);
            } else if (update.callbackQuery().data().equals("FLUSH")) {
                if (nakoplenie <= 0){
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
                    answerCallbackQuery.text("\uD83D\uDEABМинимальная сумма сбора: 1.0₽");
                    answerCallbackQuery.showAlert(true);
                    bot.execute(answerCallbackQuery);
                }else {
                    balance += nakoplenie;
                    nakoplenie = 0;
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
                    answerCallbackQuery.text("✅Накопления успешно собраны!");
                    answerCallbackQuery.showAlert(true);
                    bot.execute(answerCallbackQuery);
                    DeleteMessage deleteMessage = new DeleteMessage(update.callbackQuery().from().id(), update.callbackQuery().message().messageId());
                    bot.execute(deleteMessage);
                    System.out.println("Message Deleted!");

                }
            }
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

    }



    private void sendMessage(long chatID, String text) {
        SendMessage sendMessage = new SendMessage(chatID, text);

        KeyboardButton button = new KeyboardButton(buttons[0]);
        KeyboardButton button1 = new KeyboardButton(buttons[1]);
        KeyboardButton button2 = new KeyboardButton(buttons[2]);
        KeyboardButton button3 = new KeyboardButton(buttons[3]);
        KeyboardButton button4 = new KeyboardButton(buttons[4]);
        KeyboardButton button5 = new KeyboardButton(buttons[5]);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(button, button1);
        replyKeyboardMarkup.addRow(button2, button3);
        replyKeyboardMarkup.addRow(button4, button5);

        replyKeyboardMarkup.resizeKeyboard(true);

        sendMessage.replyMarkup(replyKeyboardMarkup);

        bot.execute(sendMessage);
    }

}

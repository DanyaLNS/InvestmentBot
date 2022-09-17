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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.*;
import java.util.Locale;
import java.util.Properties;


public class Bot {
    long id;
    float balance;

    float deposit;
    float savings;
    Time remainingTime;
    Date profileCreate;
    boolean isBanned;
    long partner;

    File file = new File("LOG.txt");
    File dataFilesPath = new File(DataBase.PATH_TO_RESOURCES);
    

    static TelegramBot bot = new TelegramBot("5663048702:AAEwHRRut1Nib4nuEK3yDizZVj9c3QC8v28");

    static String buttons[] = {"\uD83D\uDDA5 Инвестиции", "\uD83D\uDCB3 Кошелёк", "⚙️ Настройки", "\uD83D\uDC54 Партнёрам", "\uD83D\uDCE0 Калькулятор", "\uD83D\uDDD3 Обучение"};

    public Bot() {
        System.err.println("Bot Started!");
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::rss);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void rss(Update update) {

        String tempPropPath = getClientPropPath(update);
        Properties tempProp = createProperties(tempPropPath);
        initValues(tempProp);
        
        
        System.err.println("Update is working!");

        if (update.message() != null) {
            for (int i = 0; i < dataFilesPath.list().length; i++) {
                if (dataFilesPath.list()[i].startsWith(update.message().chat().id().toString())){
                    System.out.println("ADMIN mEssage send X");

                }else {
                    AdminPanel.newUser(update, bot);
                    System.out.println("ADMIN MESSAGE SEND!");
                }
            }
            System.err.println("MessageData");
            if (!update.message().text().isEmpty()) {

                String text = update.message().text();

                /*if (update.message().chat().id() == 1178010927){
                    AdminPanel.sendMessageForAdmin("Добро пожаловать в Админ панель!", bot);
                }else*/ if (text.equals("/start")) {

                    sendStartMessage(update);

                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDDA5 Инвестиции")) {
                    sendInvestmentMessage(update);
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDCB3 Кошелёк")) {
                    sendWalletMessage(update);
                } else if (text.equals("⚙️ Настройки")) {
                    sendSettingsMessage(update);
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDC54 Партнёрам")) {
                    sendPartnerMessage(update);
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDCE0 Калькулятор")) {
                    sendCalculatorMessage(update);
                    System.err.println("Sending Message");
                } else if (text.equals("\uD83D\uDDD3 Обучение")) {
                    sendTrainingMessage(update);
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

    String getClientPropPath(Update update) {
        id = update.message() != null ? update.message().chat().id() : update.callbackQuery().from().id();
        String tempClientPropPath = DataBase.getPathToPropFile(id);
        return tempClientPropPath;
    }

    Properties createProperties(String propPath) {
        Properties prop = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(propPath)) {
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    void initValues(Properties prop) {
        // Необходимо для корректной конвертации типа float
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);

        DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMMM d HH:MM:SS z yyyy", Locale.ENGLISH);

        try {
            id = Long.parseLong(prop.getProperty("id"));
            balance = format.parse(prop.getProperty("balance")).floatValue();
            deposit = format.parse(prop.getProperty("deposit")).floatValue();
            savings = format.parse(prop.getProperty("savings")).floatValue();
            remainingTime = new Time(timeFormatter.parse(prop.getProperty("remainingTime")).getTime());
            profileCreate = new Date(dateFormatter.parse(prop.getProperty("profileCreate")).getTime());
            isBanned = Boolean.getBoolean(prop.getProperty("isBanned"));
            partner = Long.parseLong(prop.getProperty("partner"));
        } catch (ParseException ex) {
            System.err.println("Parse exception");
            ex.printStackTrace();
        }
    }

    void sendStartMessage(Update update) {
        System.err.println(update);
        sendMessage(update.message().chat().id(), "©️Главное меню");
    }

    void sendInvestmentMessage(Update update) {
        String investText = MessageCreator.getInvestText(deposit, savings, remainingTime);
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), investText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton investButton = new InlineKeyboardButton("➕").callbackData("INVEST");
        InlineKeyboardButton flushButton = new InlineKeyboardButton("➖").callbackData("FLUSH");
        keyboardMarkup.addRow(investButton, flushButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }

    void sendWalletMessage(Update update) {
        String messageText = MessageCreator.getWalletText(update, remainingTime, balance, partner);
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        // TODO доделать кол-бэки
        InlineKeyboardButton investButton = new InlineKeyboardButton("Пополнить").callbackData("SOON");
        InlineKeyboardButton flushButton = new InlineKeyboardButton("Вывести").callbackData("SOON");
        keyboardMarkup.addRow(investButton, flushButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }
    void sendSettingsMessage(Update update){
        String messageText = MessageCreator.getSettingsText(0,0,0,0);
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        // TODO доделать кол-бэки
        InlineKeyboardButton notificationsButton = new InlineKeyboardButton("Уведомления").callbackData("SOON");
        InlineKeyboardButton operationButton = new InlineKeyboardButton("Операции").callbackData("SOON");
        InlineKeyboardButton informationButton = new InlineKeyboardButton("Информация").callbackData("SOON");
        InlineKeyboardButton agreementButton = new InlineKeyboardButton("Соглашение").callbackData("SOON");
        keyboardMarkup.addRow(notificationsButton, operationButton, informationButton, agreementButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }
    void sendPartnerMessage(Update update) {
        // TODO вставить ссылки после дополнения реферальной системы
        String messageText = MessageCreator.getPartnersText(deposit, partner, "", "");
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton partnerButton = new InlineKeyboardButton("➕ Как набрать партнеров?").callbackData("SOON");
        InlineKeyboardButton coopButton = new InlineKeyboardButton("Сотрудничество с нами").callbackData("SOON");
        keyboardMarkup.addRow(partnerButton, coopButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }
    void sendCalculatorMessage(Update update){
        String messageText = "Введите сумму, которую хотите рассчитать: ";
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        bot.execute(sendMessage);
        // TODO реализовать продолжение этого сообщения
    }
    void sendTrainingMessage(Update update){
        String messageText = "\u2060 \uD83C\uDF93 Попал в бота, но не знаешь, что делать? Тогда ознакомься с нашим минутным обучением:";
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton trainingButton = new InlineKeyboardButton("➕ Открыть обучение").callbackData("SOON");
        keyboardMarkup.addRow(trainingButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }
    private void sendMessage(long chatID, String text) {
        SendMessage sendMessage = new SendMessage(chatID, text);
        ReplyKeyboardMarkup replyKeyboardMarkup = createMainKeyboard();
        sendMessage.replyMarkup(replyKeyboardMarkup);
        bot.execute(sendMessage);
    }

    void callbackForInvest(Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
        answerCallbackQuery.text(MessageCreator.getInvestCallback());
        answerCallbackQuery.showAlert(true);
        bot.execute(answerCallbackQuery);
    }

    void callbackForFlush(Update update) {
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
    void callbackForSoon(Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
        answerCallbackQuery.text(MessageCreator.getSoonCallback());
        answerCallbackQuery.showAlert(true);
        bot.execute(answerCallbackQuery);
    }

    void logUpdate(Update update) {
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

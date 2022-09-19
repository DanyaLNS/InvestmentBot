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
import java.sql.Time;
import java.text.*;
import java.util.Properties;


public class Bot {
    // TODO изменить константы актуальными значениями
    static final String BOT_TOKEN = "5663048702:AAEwHRRut1Nib4nuEK3yDizZVj9c3QC8v28";
    final long ADMIN_ID = 1178010927;
    final String TRAINING_LINK = "https://telegra.ph/OBUCHENIE-BOTA-06-06";
    final String AGREEMENT_LINK = "https://telegra.ph/Licenziya-platformy-NL4-05-16";
    final String PARTNERS_INFO_LINK = "https://telegra.ph/PARTNERSKAYA-SISTEMA-06-06";
    final double PERCENT = 4.2;
    String QIWI_REQUISITES = "79600780143";
    String PAYEER_REQUISITES = "P1075229073";
    float bringOutSum;
    float addSum;
    String bringOutAccount;
    String profileCreate;
    long id;
    float balance;

    float deposit;
    float savings;
    Time remainingTime;

    boolean isBanned;
    long partner;
    /**
     * Condition - состояние клиента, которое допускает следующие значения:
     * default - стандартное состояние
     * calculate - режим расчета доходности
     * bring_out_step1 - ветка вывода средств, 1 этап запроса суммы
     * bring_out_step2 - ветка вывода средств, 2 этап запроса счета
     * add_step1 - ветка пополнения средств, 1 этап запроса суммы
     */
    String condition;

    File logs = new File("LOG.txt");

    static TelegramBot bot = new TelegramBot(BOT_TOKEN);

    static String buttons[] = {"\uD83D\uDDA5 Инвестиции", "\uD83D\uDCB3 Кошелёк", "⚙️ Настройки", "\uD83D\uDC54 Партнёрам", "\uD83D\uDCE0 Калькулятор", "\uD83D\uDDD3 Обучение"};


    public Bot() {
        System.err.println("Bot Started!");
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::rss);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void rss(Update update) {

        System.err.println("Update is working!");

        if (update.message() != null) {
            // Без этого выкидывает NPE при перезапуске бота
            if (condition == null) {
                condition = "default";
            }
            // Обработка диалоговых веток
            if (!condition.equals("default")) {
                workWithCondition(update);
            }
            updateVariables(update);
            if (!update.message().text().isEmpty()) {
                System.err.println("MessageData");
                workWithMessages(update);
            }
        } else if (update.callbackQuery() != null) {
            System.err.println("CallBackData");
            workWithCallbacks(update);
        }
        logUpdate(update);
    }

    void updateVariables(Update update) {
        id = update.message().chat().id();
        String tempPropPath = getClientPropPath(update);
        Properties tempProp = createProperties(tempPropPath);
        DataBase.rewriteVariables(tempPropPath, id, balance, deposit, savings, remainingTime, isBanned,
                                  partner, condition);
        initValues(tempProp);
    }

    String getClientPropPath(Update update) {
        id = update.message().chat().id();
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

        try {
            id = Long.parseLong(prop.getProperty("id"));
            balance = format.parse(prop.getProperty("balance")).floatValue();
            deposit = format.parse(prop.getProperty("deposit")).floatValue();
            savings = format.parse(prop.getProperty("savings")).floatValue();
            // TODO решить проблему с парсингом и этого времени
            remainingTime = new Time(timeFormatter.parse(prop.getProperty("remainingTime")).getTime());
            // TODO решить проблему с profilecreate
            profileCreate = prop.getProperty("profileCreate");
            isBanned = Boolean.getBoolean(prop.getProperty("isBanned"));
            partner = Long.parseLong(prop.getProperty("partner"));
            condition = prop.getProperty("condition");
        } catch (ParseException ex) {
            System.err.println("Parse exception");
            ex.printStackTrace();
        }
    }

    void workWithCondition(Update update) {
        if (condition.equals("calculate")) {
            try {
                float sum = Float.parseFloat(update.message().text());
                sendCalculatedMessage(update, sum);
            } catch (NumberFormatException ex) {
                System.err.println("Illegal number");
                ex.printStackTrace();
            }
        } else if (condition.equals("bring_out_step1")) {
            bringOutSum = Float.parseFloat(update.message().text());
            sendBringOutAccountMessage(update);
        } else if (condition.equals("bring_out_step2")) {
            bringOutAccount = update.message().text();
            sendBringOutEndMessage(update);
            sendBringOutToAdminMessage(update);
        }
    }

    void workWithMessages(Update update) {
        String text = update.message().text();
        if (text.equals("/start")) {
            sendStartMessage(update);
            System.err.println("Sending Message");
        } else if (text.equals("\uD83D\uDDA5 Инвестиции")) {
            sendInvestmentMessage(update);
            System.err.println("Sending Message");
        } else if (text.equals("\uD83D\uDCB3 Кошелёк")) {
            sendWalletMessage(update);
            System.err.println("Sending Message");
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

    void workWithCallbacks(Update update) {
        if (update.callbackQuery().data().equals("INVEST")) {
            callbackForInvest(update);
        } else if (update.callbackQuery().data().equals("FLUSH")) {
            callbackForFlush(update);
        } else if (update.callbackQuery().data().equals("BRINGOUT")) {
            callbackForBringOut(update);
        } else if (update.callbackQuery().data().equals("ADD")) {
            callBackForAddMoney(update);
        } else if (update.callbackQuery().data().equals("NOTIFICATIONS")) {
            callbackForNotifications(update);
        } else if (update.callbackQuery().data().equals("SOON")) {
            callbackForSoon(update);
        } else if (update.callbackQuery().data().equals("QIWI_ADD")) {
            callBackForQIWI(update);
        }
    }

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<ОБРАБОТКА ВЕТВЕЙ ДИАЛОГА>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/
    void sendCalculatedMessage(Update update, float sum) {
        condition = "default";
        String messageText = MessageCreator.getCaclulatorText(sum, PERCENT);
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        bot.execute(sendMessage);
    }

    void sendBringOutAccountMessage(Update update) {
        condition = "bring_out_step2";
        String messageText;
        if (bringOutSum > balance) {
            condition = "default";
            messageText = "На счете недостаточно средств";
        } else {
            messageText = "Введите номер счета, на который хотите вывести средства: ";
        }
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        bot.execute(sendMessage);
    }

    void sendBringOutEndMessage(Update update) {
        condition = "default";
        String messageText = "Заявка принята! Ожидайте ответа менеджера! ";
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        bot.execute(sendMessage);
    }

    void sendBringOutToAdminMessage(Update update) {
        String messageText = String.format("""
                Пользователь: %d
                Транзакция: вывод
                Номер счета:  %s""", id, bringOutAccount);
        SendMessage sendMessage = new SendMessage(ADMIN_ID, messageText);
        bot.execute(sendMessage);
    }

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<ОБРАБОТКА СООБЩЕНИЙ>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

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
        String messageText = MessageCreator.getWalletText(update, profileCreate, balance, partner);
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton investButton = new InlineKeyboardButton("Пополнить").callbackData("ADD");
        InlineKeyboardButton flushButton = new InlineKeyboardButton("Вывести").callbackData("BRINGOUT");
        keyboardMarkup.addRow(investButton, flushButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }

    void sendSettingsMessage(Update update) {
        String messageText = MessageCreator.getSettingsText(0, 0, 0, 0);
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton notificationsButton = new InlineKeyboardButton("Уведомления").callbackData("SOON");
        InlineKeyboardButton operationButton = new InlineKeyboardButton("Операции").callbackData("SOON");
        InlineKeyboardButton informationButton = new InlineKeyboardButton("Информация").url(TRAINING_LINK);
        InlineKeyboardButton agreementButton = new InlineKeyboardButton("Соглашение").url(AGREEMENT_LINK);
        keyboardMarkup.addRow(notificationsButton, operationButton);
        keyboardMarkup.addRow(informationButton, agreementButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }

    void sendPartnerMessage(Update update) {
        String messageText = MessageCreator.getPartnersText(deposit, partner, "", "");
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton partnerButton = new InlineKeyboardButton("➕ Как набрать партнеров?").url(PARTNERS_INFO_LINK);
        InlineKeyboardButton coopButton = new InlineKeyboardButton("Сотрудничество с нами").callbackData("SOON");
        keyboardMarkup.addRow(partnerButton, coopButton);
        sendMessage.replyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }

    void sendCalculatorMessage(Update update) {
        condition = "calculate";
        String messageText = "Введите сумму, которую хотите рассчитать: ";
        sendMessage(update.message().chat().id(), messageText);
    }

    void sendTrainingMessage(Update update) {
        String messageText = "\u2060 \uD83C\uDF93 Попал в бота, но не знаешь, что делать? Тогда ознакомься с нашим минутным обучением:";
        SendMessage sendMessage = new SendMessage(update.message().chat().id(), messageText);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton trainingButton = new InlineKeyboardButton("➕ Открыть обучение").url(TRAINING_LINK);
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

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<ОБРАБОТКА CALLBACK>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

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

    void callbackForBringOut(Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
        if (savings <= 0) {
            answerCallbackQuery.text(MessageCreator.getBringOutCallback());
            answerCallbackQuery.showAlert(true);
            bot.execute(answerCallbackQuery);
        } else {
            condition = "bring_out_step1";
            String messageText = "Введите сумму, которую хотите вывести: ";
            SendMessage sendMessage = new SendMessage(update.callbackQuery().from().id(), messageText);
            bot.execute(sendMessage);
        }
    }

    void callBackForAddMoney(Update update) {
        SendMessage message = new SendMessage(update.callbackQuery().from().id(), MessageCreator.getAddMoneyCallBackText());
        InlineKeyboardButton Qiwi = new InlineKeyboardButton("▪️Qiwi").callbackData("QIWI_ADD");
        InlineKeyboardButton Payeer = new InlineKeyboardButton("▪️Payeer").callbackData("PAYEER_ADD");
        InlineKeyboardButton BankCard = new InlineKeyboardButton("▪️Банковская карта").callbackData("BANKCARD_ADD");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.addRow(Qiwi);
        markup.addRow(Payeer);
        markup.addRow(BankCard);
        message.replyMarkup(markup);
        bot.execute(message);
        deleteMessage(update.callbackQuery().from().id(), update.callbackQuery().message().messageId());
    }

    void callbackForNotifications(Update update) {
        // TODO сделать включение-выключение уведомлений
    }

    void callbackForSoon(Update update) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(update.callbackQuery().id());
        answerCallbackQuery.text(MessageCreator.getSoonCallback());
        answerCallbackQuery.showAlert(true);
        bot.execute(answerCallbackQuery);
    }

    void callBackForQIWI(Update update){

        SendMessage message = new SendMessage(update.callbackQuery().from().id(), String.format("\uD83D\uDCE5 Для совершения пополнения через QIWI кошелек, переведите нужную сумму средств на номер карты указанный ниже, оставив при этом индивидуальный комментарий перевода:\n" +
                "\n" +
                "\uD83D\uDCB3 Реквизиты бота: \"%s\".\n" +
                "\uD83D\uDCAC Коментарий к переводу: \"%d\".", QIWI_REQUISITES, update.callbackQuery().from().id()));
        message.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("\uD83D\uDD04 Проверить транзакцию").callbackData("CHEK_TRANSACTION")));
        bot.execute(message);

    }

    void callBackForPAYEER(Update update){
        SendMessage message = new SendMessage(update.callbackQuery().from().id(), String.format("\uD83D\uDCE5 Для совершения пополнения через QIWI кошелек, переведите нужную сумму средств на номер карты указанный ниже, оставив при этом индивидуальный комментарий перевода:\n" +
                "\n" +
                "\uD83D\uDCB3 Реквизиты бота: \"%s\".\n" +
                "\uD83D\uDCAC Коментарий к переводу: \"%d\".", PAYEER_REQUISITES, update.callbackQuery().from().id()));
        message.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("\uD83D\uDD04 Проверить транзакцию").callbackData("CHEK_TRANSACTION")));
        bot.execute(message);
    }
    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<ПРОЧЕЕ>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

    void logUpdate(Update update) {
        try {
            String updateS = update.toString();
            FileWriter writer = new FileWriter(logs);
            writer.write(updateS.replaceAll(",", "\n"));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteMessage(long chatID, int messageID){
        DeleteMessage delMessage = new DeleteMessage(chatID, messageID);
        bot.execute(delMessage);
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

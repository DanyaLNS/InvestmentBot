package org.bot.investmentBot;

import com.pengrad.telegrambot.model.Update;

import java.sql.Time;

public class MessageCreator {

    public static String getInvestText(float deposit, float savings, Time remainingTime) {
        return "▪️ Открывайте свой вклад ниже, а после получайте прибыль с него и собирайте ее в данном разделе:\n" +
                "\n" +
                "\uD83D\uDCE0 Процент от вклада: 4.2%\n" +
                "⏱ Время доходности: 24 часа\n" +
                "\uD83D\uDCC6 Срок вклада: Пожизненно\n" +
                "\n" + String.format("\uD83D\uDCB3 Ваш вклад: %.2f₽\n", deposit) +
                String.format("\uD83D\uDCB5 Накопление: %.2f₽\n", savings) +
                "\n" +
                "\uD83E\uDDED Время до сбора средств: " + remainingTime;
    }

    public static String getWalletText(Update update, Time remainingTime, float balance, int partners) {
        return String.format(
                "\uD83E\uDD16 Ваш ID: %d\n" +
                        "\uD83D\uDCC6 Профиль создан: %s\n" +
                        "\uD83D\uDCB3 Ваш баланс: %.2f\n" +
                        "\uD83D\uDC65 Партнеров: %d чел.", update.message().from().id(), remainingTime, balance, partners);
    }
    public static String getInvestCallback(){
        return "\uD83D\uDEABПополните баланс, минимальная сумма для инвестиции: 100.0₽";
    }
    public static String getFlushCallback(){
        return "\uD83D\uDEABМинимальная сумма сбора: 1.0₽";
    }
}

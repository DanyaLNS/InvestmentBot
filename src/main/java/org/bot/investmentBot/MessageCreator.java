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

    public static String getWalletText(Update update, Time remainingTime, float balance, long partner) {
        return String.format(
                "\uD83E\uDD16 Ваш ID: %d\n" +
                        "\uD83D\uDCC6 Профиль создан: %s\n\n" +
                        "\uD83D\uDCB3 Ваш баланс: %.2f\n" +
                        "\uD83D\uDC65 Партнеров: %d чел.", update.message().from().id(), remainingTime, balance, partner);
    }

    public static String getPartnersText(float deposit, long partners, String referalLink, String outerReferalLink) {
        return
                "▪️Партнерская программа создана для того чтобы получить клиенту дополнительный источник дохода, приглашайте людей по своей ссылке\n" +
                        "\n" +
                        "\uD83D\uDCB8 Процент с инвестиций: 25.0% \n" +
                        "\uD83D\uDCB0 Процент с вывода: 15.0% \n" +
                        "\n" +
                        "\uD83D\uDCB3 Всего заработано: " + deposit + "Р \n" +
                        "\n" +
                        "\uD83D\uDC65 Партнеров: " + partners + "\n" +
                        "\n" +
                        "\uD83D\uDD17 Ваша реф-ссылка: \n" + referalLink +
                        "\n" +
                        "〽️ Внешняя реф-ссылка: " + outerReferalLink;
    }

    public static String getSettingsText(int daysOfWork, int amountOfInvestors,
                                         int newInvestors, int onlineInvestors) {
        return String.format("▪️ Вы попали в раздел настройки бота, здесь вы можете посмотреть статистику, а также узнать нужную информацию или отключить новые уведомления в нашем боте \n" +
                "\n" +
                "\uD83C\uDF10 Дней работаем: %d\n" +
                "▪️ Всего инвесторов: %d\n" +
                "▪️ Новых за 24 часа: %d\n" +
                "▪️ Онлайн: %d", daysOfWork, amountOfInvestors, newInvestors, onlineInvestors);
    }

    public static String getCaclulatorText(float sum, float percent) {
        double day = compoundInterest(sum, percent, 1);
        double mounth = compoundInterest(sum, percent, 30);
        double year = compoundInterest(sum, percent, 360);

        return String.format("\uD83D\uDCB1 В данном разделе Вы сумеете рассчитать Вашу прибыль, от суммы вашей инвестиции в наш проект:\n" +
                "\n" +
                "\uD83D\uDCB5 Ваша инвестиция: %f₽\n" +
                "\n" +
                "▪️ Прибыль в сутки: %f₽\n" +
                "▪️ Прибыль в месяц: %f₽\n" +
                "▪️ Прибыль в год: %f₽", sum, day, mounth, year);
    }

    private static double compoundInterest(float sum, float percent, int period) {
        return Math.round(sum * Math.pow((1 + 0.01 * percent), period));
    }

    public static String getInvestCallback() {
        return "\uD83D\uDEABПополните баланс, минимальная сумма для инвестиции: 100.0₽";
    }

    public static String getFlushCallback() {
        return "\uD83D\uDEABМинимальная сумма сбора: 1.0₽";
    }

    public static String getSoonCallback() {
        return "Этот раздел будет доступен позже";
    }

}

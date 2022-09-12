package org.bot.investmentBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import java.sql.Time;

public class Commands {

    private Update update;
    private final TelegramBot bot;





    public Commands(Update update, TelegramBot bot) {
        this.update = update;
        this.bot = bot;
    }

    public void start() {

    }



    public void inputMoneys(){

    }

    public void outputMoneys(){

    }

    public void calc(){

    }

    public void payment(){

    }

    public void settings(){

    }

    public void referal() {
    }

    public void help() {
    }


    private static boolean isInt(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}

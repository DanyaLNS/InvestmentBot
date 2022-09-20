package org.bot.investmentBot;

import java.io.*;
import java.sql.Time;
import java.util.Date;
import java.util.Properties;


public class DataBase {
    final static String PATH_TO_RESOURCES = "resources/";
    static String fileName;
    static Date profileCreate;

    public static String getPathToPropFile(long id) {
        fileName = id + ".properties";
        File file = new File(PATH_TO_RESOURCES + fileName);
        try {
            if (file.createNewFile()) {
                createNewPropFile(file, id);
                System.out.println(profileCreate);
            }
        } catch (IOException ex) {
            System.err.println("IO ex");
        } finally {
            return PATH_TO_RESOURCES + fileName;
        }
    }

    private static void createNewPropFile(File file, long id) throws IOException {
        float balance = 0;
        float deposit = 0;
        float savings = 0;
        Time remainingTime = new Time(0, 0, 0);
        profileCreate = new Date();
        boolean isBanned = false;
        long partner = 0;
        String condition = "default";
        String prop = fillFile(id, balance, deposit,
                savings, remainingTime, profileCreate,
                isBanned, partner, condition);
        FileWriter writer = new FileWriter(file);
        writer.write(prop);
        writer.flush();
        writer.close();
    }

    private static String fillFile(long id, float balance, float deposit,
                                   float savings, Time remainingTime, Date profileCreate,
                                   boolean isBanned, long partner, String condition) {
        return String.format("""
                        id = %d
                        balance = %f
                        deposit = %f
                        savings = %f
                        remainingTime = %s
                        profileCreate = %s
                        isBanned = %b
                        partner = %d
                        condition = %s
                        """,
                id, balance, deposit, savings, remainingTime, profileCreate, isBanned, partner, condition);
    }

    private static String fillFile(long id, float balance, float deposit,
                                   float savings, long remainingTime, String profileCreate,
                                   boolean isBanned, long partner, String condition) {
        return String.format("""
                        id = %d
                        balance = %f
                        deposit = %f
                        savings = %f
                        remainingTime = %s
                        profileCreate = %s
                        isBanned = %b
                        partner = %d
                        condition = %s
                        """,
                id, balance, deposit, savings, remainingTime, profileCreate, isBanned, partner, condition);
    }


    public static void rewriteVariables(String fileName, long id, float balance, float deposit,
                                        float savings, long remainingTime,
                                        boolean isBanned, long partner, String condition) {
        try {
            Properties properties = new Properties();
            try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
                properties.load(fileInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = new File(fileName);
            String prop = fillFile(id, balance, deposit,
                    savings, remainingTime, properties.getProperty("profileCreate"),
                    isBanned, partner, condition);
            FileWriter writer = new FileWriter(file);
            writer.write(prop);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.err.println("IO exception");
            ex.printStackTrace();
        }
    }
}

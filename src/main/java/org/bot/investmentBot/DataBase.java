package org.bot.investmentBot;

import java.io.*;
import java.sql.Time;
import java.util.Date;
import java.util.Properties;


public class DataBase {
    final static String PATH_TO_RESOURCES = "resources/";

    public static String getPathToPropFile(long id) {
        String fileName = id + ".properties";
        File file = new File(PATH_TO_RESOURCES + fileName);
        try {
            if (file.createNewFile()) {
                createNewPropFile(file, id);
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
        Date profileCreate = new Date();
        boolean isBanned = false;
        long partner = 0;
        String prop = fillFile(id, balance, deposit,
                savings, remainingTime, profileCreate,
                isBanned, partner);
        FileWriter writer = new FileWriter(file);
        writer.write(prop);
        writer.flush();
        writer.close();
    }

    private static String fillFile(long id, float balance, float deposit,
                                   float savings, Time remainingTime, Date profileCreate,
                                   boolean isBanned, long partner) {
        return String.format("""
                        id = %d
                        balance = %f
                        deposit = %f
                        savings = %f
                        remainingTime = %s
                        profileCreate = %s
                        isBanned = %b
                        partner = %d
                        """,
                id, balance, deposit, savings, remainingTime, profileCreate, isBanned, partner);
    }

    public static void rewriteVariables(String fileName, long id, float balance, float deposit,
                                        float savings, Time remainingTime, Date profileCreate,
                                        boolean isBanned, long partner) {
        try (FileInputStream in = new FileInputStream(fileName);
             FileOutputStream out = new FileOutputStream(fileName)) {
            Properties props = new Properties();
            props.load(in);

            props.setProperty("id", Long.toString(id));
            props.setProperty("balance", Float.toString(balance));
            props.setProperty("deposit", Float.toString(deposit));
            props.setProperty("savings", Float.toString(savings));
            props.setProperty("remainingTime", remainingTime.toString());
            props.setProperty("profileCreate", profileCreate.toString());
            props.setProperty("isBanned", Boolean.toString(isBanned));
            props.setProperty("partner", Long.toString(partner));

            props.store(out, null);
        } catch (FileNotFoundException ex) {
            System.err.println("File not found");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("IO exception");
            ex.printStackTrace();
        }
    }
}

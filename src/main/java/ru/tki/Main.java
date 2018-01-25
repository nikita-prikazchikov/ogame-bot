package ru.tki;

import ru.tki.brain.Mainframe;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        BotConfigMainReader reader = new BotConfigMainReader();
        BotConfigMain config = reader.getPropValues();
        ContextHolder.setBotConfigMain(config);

        DriverManager driverManager = new DriverManager();
        ContextHolder.setDriverManager(driverManager);

        Mainframe mainframe = new Mainframe();
        mainframe.start();
        driverManager.closeDriver();
    }
}

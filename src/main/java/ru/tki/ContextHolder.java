package ru.tki;

import org.openqa.selenium.WebDriver;

public class ContextHolder {
    static DriverManager driverManager;
    static BotConfigMain botConfigMain;

    public ContextHolder() {
    }

    public static WebDriver getDriver() {
        return driverManager.getDriver();
    }

    public static DriverManager getDriverManager() {
        return driverManager;
    }

    public static void setDriverManager(DriverManager driverManager) {
        ContextHolder.driverManager = driverManager;
    }

    public static BotConfigMain getBotConfigMain() {
        return botConfigMain;
    }

    public static void setBotConfigMain(BotConfigMain botConfigMain) {
        ContextHolder.botConfigMain = botConfigMain;
    }
}

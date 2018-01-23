package ru.tki;

import org.openqa.selenium.WebDriver;

public class ContextHolder {
    static DriverManager driverManager;
    static BotConfigMain botConfigMain;
    // value of hours to take resources off the planet
    static Integer       productionTime;

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

    public static Integer getProductionTime() {
        return productionTime;
    }

    public static void setProductionTime(Integer productionTime) {
        ContextHolder.productionTime = productionTime;
    }
}

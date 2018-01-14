package ru.tki;

import ru.tki.executor.Navigation;
import ru.tki.po.LoginPage;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Let's the magic start!");

        BotConfigMainReader reader = new BotConfigMainReader();
        BotConfigMain config = reader.getPropValues();
        ContextHolder.setBotConfigMain(config);

        DriverManager driverManager = new DriverManager();
        ContextHolder.setDriverManager(driverManager);

        Navigation navigation = new Navigation();
        navigation.openHomePage();

        LoginPage loginPage = new LoginPage();
        loginPage.login();

        driverManager.closeDriver();
    }
}

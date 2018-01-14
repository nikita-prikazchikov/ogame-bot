package ru.tki.executor;

import org.openqa.selenium.WebDriver;
import ru.tki.ContextHolder;

public class Navigation {

    public void openHomePage(){
        WebDriver webDriver = ContextHolder.getDriver();
        webDriver.get(ContextHolder.getBotConfigMain().getUrl());
    }
}

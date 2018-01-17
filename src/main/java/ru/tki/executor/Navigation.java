package ru.tki.executor;

import org.openqa.selenium.WebDriver;
import ru.tki.ContextHolder;
import ru.tki.po.components.LeftMenuComponent;

public class Navigation {

    public LeftMenuComponent leftMenu;

    public Navigation() {
        super();
        leftMenu = new LeftMenuComponent();
    }

    public void openHomePage(){
        WebDriver webDriver = ContextHolder.getDriver();
        webDriver.get(ContextHolder.getBotConfigMain().getUrl());
    }
}

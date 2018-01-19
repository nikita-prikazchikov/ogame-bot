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

    public void openOverview() {
        leftMenu.openOverview();
    }

    public void openShipyard() {
        leftMenu.openShipyard();
    }

    public void openResearch() {
        leftMenu.openResearch();
    }

    public void openFleet() {
        leftMenu.openFleet();
    }

    public void openDefense() {
        leftMenu.openDefense();
    }

    public void openResources() {
        leftMenu.openResources();
    }

    public void openGalaxy() {
        leftMenu.openGalaxy();
    }

    public void openTraderOverview() {
        leftMenu.openTraderOverview();
    }

    public void openFactory() {
        leftMenu.openFactory();
    }
}

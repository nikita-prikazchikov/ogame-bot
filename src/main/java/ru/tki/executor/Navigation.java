package ru.tki.executor;

import org.openqa.selenium.WebDriver;
import ru.tki.ContextHolder;
import ru.tki.models.AbstractPlanet;
import ru.tki.po.components.LeftMenuComponent;
import ru.tki.po.components.MyWorldsComponent;

public class Navigation {

    public LeftMenuComponent leftMenu;
    public MyWorldsComponent myWorldsComponent;

    public Navigation() {
        super();
        leftMenu = new LeftMenuComponent();
        myWorldsComponent = new MyWorldsComponent();
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

    public void openFleetMove() {
        leftMenu.openFleetMove();
    }

    public void selectPlanet(AbstractPlanet planet) {
        myWorldsComponent.selectPlanet(planet);
    }
}

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
        webDriver.get(ContextHolder.getBotConfigMain().URL);
    }

    public void openOverview() {
        if (!leftMenu.onPage("page=overview"))
        leftMenu.openOverview();
    }

    public void openShipyard() {
        if (!leftMenu.onPage("page=shipyard"))
        leftMenu.openShipyard();
    }

    public void openResearch() {
        if (!leftMenu.onPage("page=research"))
        leftMenu.openResearch();
    }

    public void openFleet() {
        if (!leftMenu.onPage("page=fleet1"))
        leftMenu.openFleet();
    }

    public void openDefense() {
        if (!leftMenu.onPage("page=defense"))
        leftMenu.openDefense();
    }

    public void openResources() {
        if (!leftMenu.onPage("page=resources"))
        leftMenu.openResources();
    }

    public void openGalaxy() {
        if (!leftMenu.onPage("page=galaxy"))
        leftMenu.openGalaxy();
    }

    public void openTraderOverview() {
        if (!leftMenu.onPage("page=traderOverview"))
        leftMenu.openTraderOverview();
    }

    public void openFactory() {
        if (!leftMenu.onPage("page=station"))
        leftMenu.openFactory();
    }

    public void openFleetMove() {
        if (!leftMenu.onPage("page=movement"))
        leftMenu.openFleetMove();
    }

    public void selectPlanet(AbstractPlanet planet) {
        if(!myWorldsComponent.isPlanetSelected(planet)) {
            myWorldsComponent.selectPlanet(planet);
        }
    }
}

package ru.tki.po.components;

import org.openqa.selenium.By;
import ru.tki.po.PageObject;

public class LeftMenuComponent extends PageObject {

    private final String itemLocator = "//div[@id='links']//a[contains(@class, 'menubutton') and contains(., '%s')]";

    private final String ITEM_OVERVIEW        = "Обзор";
    private final String ITEM_RESOURCES       = "Сырьё";
    private final String ITEM_STATION         = "Фабрики";
    private final String ITEM_TRADER_OVERVIEW = "Скупщик";
    private final String ITEM_RESEARCH        = "Исследования";
    private final String ITEM_SHIPYARD        = "Верфь";
    private final String ITEM_DEFENSE         = "Оборона";
    private final String ITEM_FLEET           = "Флот";
    private final String ITEM_GALAXY          = "Галактика";

    private void openPage(String value){
        getElement(By.xpath(String.format(itemLocator, value))).click();
    }

    public void openOverview(){
        this.openPage(ITEM_OVERVIEW);
    }

    public void openResources(){
        this.openPage(ITEM_RESOURCES);
    }

    public void openStation(){
        this.openPage(ITEM_STATION);
    }

    public void openTraderOverview(){
        this.openPage(ITEM_TRADER_OVERVIEW);
    }

    public void openResearch(){
        this.openPage(ITEM_RESEARCH);
    }

    public void openShipyard(){
        this.openPage(ITEM_SHIPYARD);
    }

    public void openDefense(){
        this.openPage(ITEM_DEFENSE);
    }

    public void openFleet(){
        this.openPage(ITEM_FLEET);
    }

    public void openGalaxy(){
        this.openPage(ITEM_GALAXY);
    }


}

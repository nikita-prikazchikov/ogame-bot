package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.po.components.LeftMenuComponent;
import ru.tki.po.components.MyWorldsComponent;
import ru.tki.po.components.ResourcesComponent;

public class BasePage extends PageObject {

    private static final By ATTACK_ALERT = By.cssSelector("#attack_alert");

    public LeftMenuComponent leftMenu;
    public ResourcesComponent resources;
    public MyWorldsComponent myWorlds;

    public BasePage() {
        super();
        leftMenu = new LeftMenuComponent();
        resources = new ResourcesComponent();
        myWorlds = new MyWorldsComponent();
    }

    public boolean isUnderAttack() {
        return isElementDisplayed(ATTACK_ALERT) && !getElement(ATTACK_ALERT).getAttribute("class").contains("wreckField");
    }
}

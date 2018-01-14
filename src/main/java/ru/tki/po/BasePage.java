package ru.tki.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.tki.po.components.LeftMenu;
import ru.tki.po.components.MyWorlds;
import ru.tki.po.components.Resources;

public class BasePage extends PageObject {

    public LeftMenu leftMenu;
    public Resources resources;
    public MyWorlds myWorlds;

    public BasePage() {
        super();
        leftMenu = new LeftMenu();
        resources = new Resources();
        myWorlds = new MyWorlds();
    }
}

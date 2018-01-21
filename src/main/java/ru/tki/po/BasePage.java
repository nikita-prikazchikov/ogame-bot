package ru.tki.po;

import ru.tki.po.components.LeftMenuComponent;
import ru.tki.po.components.MyWorldsComponent;
import ru.tki.po.components.ResourcesComponent;

public class BasePage extends PageObject {

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
        return false;
    }
}

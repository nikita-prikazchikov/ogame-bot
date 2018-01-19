package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.models.types.BuildingType;

import java.util.HashMap;
import java.util.Map;


public class ResourcesPage extends PageObject {

    private static final Map<BuildingType, By> elements = new HashMap<BuildingType, By>() {{
        put(BuildingType.METAL_MINE, By.cssSelector("#button1"));
        put(BuildingType.CRYSTAL_MINE, By.cssSelector("#button2"));
        put(BuildingType.DEUTERIUM_MINE, By.cssSelector("#button3"));
        put(BuildingType.SOLAR_PLANT, By.cssSelector("#button4"));
        put(BuildingType.SOLAR_SATELLITE, By.cssSelector("#button6"));
        put(BuildingType.METAL_STORAGE, By.cssSelector("#button7"));
        put(BuildingType.CRYSTAL_STORAGE, By.cssSelector("#button8"));
        put(BuildingType.DEUTERIUM_STORAGE, By.cssSelector("#button9"));
    }};

    public void build(BuildingType type) {
        getElement(getElement(elements.get(type)), By.cssSelector(".fastBuild")).click();
    }

    public void select(BuildingType type) {
        getElement(getElement(elements.get(type)), By.cssSelector("#details")).click();
    }

    public int getBuildingLevel(BuildingType type) {
        return Integer.parseInt(
                getElement(getElement(elements.get(type)), By.cssSelector(".level")).getText().trim());
    }
}

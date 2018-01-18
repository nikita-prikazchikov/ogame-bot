package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.models.StationType;

import java.util.HashMap;
import java.util.Map;


public class StationsPage extends PageObject {

    private static final Map<StationType, By> stations = new HashMap<StationType, By>() {{
        put(StationType.ROBOTS_FACTORY, By.cssSelector("#button0"));
        put(StationType.SHIPYARD, By.cssSelector("#button1"));
        put(StationType.RESEARCH_LAB, By.cssSelector("#button2"));
        put(StationType.ALLIANCE_WAREHOUSE, By.cssSelector("#button3"));
        put(StationType.MISSILE_SILOS, By.cssSelector("#button4"));
        put(StationType.NANITE_FACTORY, By.cssSelector("#button5"));
        put(StationType.TERRAFORMER, By.cssSelector("#button6"));
        put(StationType.SPACE_DOCK, By.cssSelector("#button7"));
    }};

    public void build(StationType type) {
        helper.getElement(helper.getElement(stations.get(type)), By.cssSelector(".fastBuild")).click();
    }

    public void select(StationType type) {
        helper.getElement(helper.getElement(stations.get(type)), By.cssSelector(".detail_button ")).click();
    }

    public int getBuildingLevel(StationType type) {
        return Integer.parseInt(
                helper.getElement(helper.getElement(stations.get(type)), By.cssSelector(".level")).getText().trim());
    }
}

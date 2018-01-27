package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.models.Factories;
import ru.tki.models.types.FactoryType;

import java.util.HashMap;
import java.util.Map;


public class FactoriesPage extends PageObject {

    private static final Map<FactoryType, By> elements = new HashMap<FactoryType, By>() {{
        put(FactoryType.ROBOTS_FACTORY, By.cssSelector("#button0"));
        put(FactoryType.SHIPYARD, By.cssSelector("#button1"));
        put(FactoryType.RESEARCH_LAB, By.cssSelector("#button2"));
        put(FactoryType.ALLIANCE_WAREHOUSE, By.cssSelector("#button3"));
        put(FactoryType.MISSILE_SILOS, By.cssSelector("#button4"));
        put(FactoryType.NANITE_FACTORY, By.cssSelector("#button5"));
        put(FactoryType.TERRAFORMER, By.cssSelector("#button6"));
        put(FactoryType.SPACE_DOCK, By.cssSelector("#button7"));
    }};

    public void build(FactoryType type) {
        getElement(getElement(elements.get(type)), By.cssSelector(".fastBuild")).click();
    }

    public void select(FactoryType type) {
        getElement(getElement(elements.get(type)), By.cssSelector(".detail_button ")).click();
    }

    public int getBuildingLevel(FactoryType type) {
        return Integer.parseInt(
                getElement(getElement(elements.get(type)), By.cssSelector(".level")).getText().trim());
    }

    public Factories getFactories(){
        Factories factories = new Factories();
        for(FactoryType type : FactoryType.values()){
            factories.set(type, getBuildingLevel(type));
        }
        return factories;
    }
}

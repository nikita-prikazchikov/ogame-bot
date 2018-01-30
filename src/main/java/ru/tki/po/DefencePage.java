package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.models.Defence;
import ru.tki.models.types.DefenceType;
import ru.tki.utils.DataParser;

import java.util.HashMap;
import java.util.Map;


public class DefencePage extends PageObject {

    private static final Map<DefenceType, By> elements = new HashMap<DefenceType, By>() {{
        put(DefenceType.ROCKET, By.cssSelector("#defense1"));
        put(DefenceType.LIGHT_LASER, By.cssSelector("#defense2"));
        put(DefenceType.HEAVY_LASER, By.cssSelector("#defense3"));
        put(DefenceType.GAUSS, By.cssSelector("#defense4"));
        put(DefenceType.ION, By.cssSelector("#defense5"));
        put(DefenceType.PLASMA, By.cssSelector("#defense6"));
        put(DefenceType.SMALL_SHIELD, By.cssSelector("#defense7"));
        put(DefenceType.BIG_SHIELD, By.cssSelector("#defense8"));
        put(DefenceType.DEFENCE_MISSILE, By.cssSelector("#defense9"));
        put(DefenceType.MISSILE, By.cssSelector("#defense10"));
    }};

    public void select(DefenceType type) {
        getElement(getElement(elements.get(type)), By.cssSelector(".detail_button ")).click();
    }

    public int getDefenceCount(DefenceType type) {
        return DataParser.parseResource(
                getElement(getElement(elements.get(type)), By.cssSelector(".level")).getText().trim());
    }

    public Defence getDefence(){
        Defence defence = new Defence();
        for(DefenceType type : DefenceType.values()){
            defence.set(type, getDefenceCount(type));
        }
        return defence;
    }
}

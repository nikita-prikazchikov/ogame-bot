package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.models.types.ShipType;

import java.util.HashMap;
import java.util.Map;


public class ShipyardPage extends PageObject {

    private static final Map<ShipType, By> elements = new HashMap<ShipType, By>() {{
        put(ShipType.LIGHT_FIGHTER, By.cssSelector("#battleships #button1"));
        put(ShipType.HEAVY_FIGHTER, By.cssSelector("#battleships #button2"));
        put(ShipType.CRUISER, By.cssSelector("#battleships #button3"));
        put(ShipType.BATTLESHIP, By.cssSelector("#battleships #button4"));
        put(ShipType.BATTLECRUISER, By.cssSelector("#battleships #button5"));
        put(ShipType.BOMBER, By.cssSelector("#battleships #button6"));
        put(ShipType.DESTROYER, By.cssSelector("#battleships #button7"));
        put(ShipType.DEATHSTAR, By.cssSelector("#battleships #button8"));

        put(ShipType.SMALL_CARGO, By.cssSelector("#civilships #button1"));
        put(ShipType.LARGE_CARGO, By.cssSelector("#civilships #button2"));
        put(ShipType.COLONY_SHIP, By.cssSelector("#civilships #button3"));
        put(ShipType.RECYCLER, By.cssSelector("#civilships #button4"));
        put(ShipType.ESPIONAGE_PROBE, By.cssSelector("#civilships #button5"));
        put(ShipType.SOLAR_SATELLITE, By.cssSelector("#civilships #button6"));

    }};

    public void select(ShipType type) {
        getElement(getElement(elements.get(type)), By.cssSelector(".detail_button")).click();
    }

    public int getShipsCount(ShipType type) {
        return Integer.parseInt(
                getElement(getElement(elements.get(type)), By.cssSelector(".level")).getText().trim());
    }
}

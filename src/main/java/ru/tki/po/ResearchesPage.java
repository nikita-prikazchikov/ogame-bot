package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.models.types.ResearchType;

import java.util.HashMap;
import java.util.Map;


public class ResearchesPage extends PageObject {

    private static final Map<ResearchType, By> elements = new HashMap<ResearchType, By>() {{
        put(ResearchType.ENERGY, By.cssSelector(".research113"));
        put(ResearchType.LASER, By.cssSelector(".research120"));
        put(ResearchType.ION, By.cssSelector(".research121"));
        put(ResearchType.HYPER, By.cssSelector(".research114"));
        put(ResearchType.PLASMA, By.cssSelector(".research122"));
        put(ResearchType.REACTIVE_ENGINE, By.cssSelector(".research115"));
        put(ResearchType.IMPULSE_ENGINE, By.cssSelector(".research117"));
        put(ResearchType.HYPER_ENGINE, By.cssSelector(".research118"));
        put(ResearchType.ESPIONAGE, By.cssSelector(".research106"));
        put(ResearchType.COMPUTER, By.cssSelector(".research108"));
        put(ResearchType.ASTROPHYSICS, By.cssSelector(".research124"));
        put(ResearchType.MIS, By.cssSelector(".research123"));
        put(ResearchType.GRAVITY, By.cssSelector(".research199"));
        put(ResearchType.WEAPON, By.cssSelector(".research109"));
        put(ResearchType.SHIELDS, By.cssSelector(".research110"));
        put(ResearchType.ARMOR, By.cssSelector(".research111"));

    }};

    public void build(ResearchType type) {
        getElement(getElement(elements.get(type)), By.cssSelector(".fastBuild")).click();
    }

    public void select(ResearchType type) {
        getElement(getElement(elements.get(type)), By.cssSelector(".detail_button")).click();
    }

    public int getResearchLevel(ResearchType type) {
        return Integer.parseInt(
                getElement(getElement(elements.get(type)), By.cssSelector(".level")).getText().trim());
    }
}

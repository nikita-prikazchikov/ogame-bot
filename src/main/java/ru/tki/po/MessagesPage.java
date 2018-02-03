package ru.tki.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.models.Coordinates;
import ru.tki.models.EnemyPlanet;
import ru.tki.models.Resources;
import ru.tki.utils.DataParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class MessagesPage extends PageObject {

    private static final By SPY_REPORTS = By.cssSelector("#fleetsgenericpage");

    private static final By NEW_MESSAGES  = By.cssSelector(".msg.msg_new");
    private static final By TITLE         = By.cssSelector(".msg_title");
    private static final By RESOURCE_SPAN = By.cssSelector(".resspan");
    private static final By TEXT          = By.cssSelector("span.ctn");

    private Pattern metal     = Pattern.compile("Металл:\\s*([\\d.]+)");
    private Pattern crystal   = Pattern.compile("Кристалл:\\s*([\\d.]+)");
    private Pattern deuterium = Pattern.compile("Дейтерий:\\s*([\\d.]+)");
    private Pattern resources = Pattern.compile("Сырьё:\\s*([\\d.]+)");
    private Pattern fleets    = Pattern.compile("Флоты:\\s*([\\d.]+)");
    private Pattern defence   = Pattern.compile("Оборона:\\s*([\\d.]+)");

    public List<EnemyPlanet> parseSpyReports() {
        List<EnemyPlanet> planets = new ArrayList<>();
        planets.addAll(findElements(NEW_MESSAGES).stream().map(this::parseSpyReport).collect(Collectors.toList()));
        return planets;
    }

    private EnemyPlanet parseSpyReport(WebElement element) {
        EnemyPlanet planet = new EnemyPlanet();

        planet.setCoordinates(new Coordinates(getElement(element, TITLE).getText()));

        Resources resources = planet.getResources();

        resources.setMetal(DataParser.parseResource(findMatch(element, metal)));
        resources.setCrystal(DataParser.parseResource(findMatch(element, crystal)));
        resources.setDeuterium(DataParser.parseResource(findMatch(element, deuterium)));

        String res = findMatch(element, fleets);
        if (null != res) {
            planet.fleetDiscovered = true;
            planet.setFleetCost((long) DataParser.parseResource(res));
        }
        res = findMatch(element, defence);
        if (null != res) {
            planet.defenceDiscovered = true;
            planet.setDefenceCost((long) DataParser.parseResource(res));
        }

        return planet;
    }

    private String findMatch(WebElement element, Pattern pattern) {
        List<WebElement> elements = findElements(element, TEXT);
        for (WebElement el : elements) {
            Matcher m = pattern.matcher(el.getText());
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }
}

package ru.tki.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.ContextHolder;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.CheckColonyTask;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.ShipType;
import ru.tki.utils.DataParser;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FleetDetailsPage extends PageObject {

    private static final By CURRENT_FLEETS_COUNT        = By.cssSelector(".fleetStatus .fleetSlots .current");
    private static final By ALL_FLEETS_COUNT            = By.cssSelector(".fleetStatus .fleetSlots .all");
    private static final By EXPEDITION_FLEETS_COUNT     = By.cssSelector(".fleetStatus .expSlots .current");
    private static final By ALL_EXPEDITION_FLEETS_COUNT = By.cssSelector(".fleetStatus .expSlots .all");

    private static final By FLEET_DETAILS      = By.cssSelector(".fleetDetails");
    private static final By CLOSED_FLEET       = By.cssSelector(".fleetDetails.detailsClosed");
    private static final By OPEN_CLOSE_DETAILS = By.cssSelector(".openCloseDetails");

    private static final String FLEET_DETAILS_VALUE = ".//tr[./td[contains(.,'%s')]]/td[@class='value']";

    private static final String RESOURCE_METAL = "Металл";
    private static final String RESOURCE_CRYSTAL = "Кристалл";
    private static final String RESOURCE_DEUTERIUM = "Дейтерий";

    private static final Map<ShipType, String> ships = new HashMap<ShipType, String>() {{
        put(ShipType.LIGHT_FIGHTER, "Лёгкий истребитель");
        put(ShipType.HEAVY_FIGHTER, "Тяжёлый истребитель");
        put(ShipType.CRUISER, "Крейсер");
        put(ShipType.BATTLESHIP, "Линкор");
        put(ShipType.BATTLECRUISER, "Линейный крейсер");
        put(ShipType.BOMBER, "Бомбардировщик");
        put(ShipType.DESTROYER, "Уничтожитель");
        put(ShipType.DEATHSTAR, "Звезда смерти");

        put(ShipType.SMALL_CARGO, "Малый транспорт");
        put(ShipType.LARGE_CARGO, "Большой транспорт");
        put(ShipType.COLONY_SHIP, "Колонизатор");
        put(ShipType.RECYCLER, "Переработчик");
        put(ShipType.ESPIONAGE_PROBE, "Шпионский зонд");
    }};

    public List<FleetAction> getFleetActions(Empire empire) {
        List<FleetAction> fleetActions = new ArrayList<>();
        openClosedFleetDetails();
        if (isElementExists(FLEET_DETAILS)) {
            fleetActions.addAll(findElements(FLEET_DETAILS).stream().map(fleetElement ->
                    getFleetAction(empire, fleetElement)).collect(Collectors.toList()));
        }
        return fleetActions;
    }

    private FleetAction getFleetAction(Empire empire, WebElement element) {
        FleetAction fleetAction = new FleetAction();
        fleetAction.setMissionType(getMissionType(element));
        if (fleetAction.getMissionType() == MissionType.EXPEDITION) {
            empire.addActiveExpedition();
        }
        AbstractPlanet planet = empire.findPlanet(getFrom(element));
        if (null != planet) {
            fleetAction.setPlanet(planet);
        }

        Coordinates targetCoordinates = getTarget(element);
        planet = empire.findPlanet(targetCoordinates);
        if (null != planet) {
            fleetAction.setTargetPlanet(planet);
        } else {
            fleetAction.setTargetPlanet(new Planet(targetCoordinates, ""));
        }
        if (isReturnFlight(element)) {
            Duration duration = getDuration(element);
            if (null != duration) {
                fleetAction.addDuration(duration);
            }
        } else {
            Duration duration = getDuration(element);
            if (null != duration) {
                fleetAction.setDurationOfFlight(duration);
            }
            duration = getDurationReturn(element);
            if (null != duration) {
                fleetAction.addDuration(duration);
            }
        }
        if (fleetAction.getMissionType() == MissionType.COLONIZATION) {
            fleetAction.addTask(new CheckColonyTask(empire, fleetAction.getTargetPlanet(), ContextHolder.getBotConfigMain().getColonyMinSize()));
        }
        collectFleetDetails(element, fleetAction);
        return fleetAction;
    }

    private void collectFleetDetails(WebElement element, FleetAction action){
        WebElement route = getElement(element, By.cssSelector(".route a"));
        hoverElement(route);
        String fleetId = route.getAttribute("rel");

        waitForWebElementIsDisplayed(By.cssSelector("#" + fleetId));
        WebElement fleetDetails = getElement(By.cssSelector("#" + fleetId));

        Resources resources = new Resources();
        resources.setMetal(DataParser.parseResource(
                getElement(fleetDetails, By.xpath(String.format(FLEET_DETAILS_VALUE, RESOURCE_METAL))).getText()));
        resources.setCrystal(DataParser.parseResource(
                getElement(fleetDetails, By.xpath(String.format(FLEET_DETAILS_VALUE, RESOURCE_CRYSTAL))).getText()));
        resources.setDeuterium(DataParser.parseResource(
                getElement(fleetDetails, By.xpath(String.format(FLEET_DETAILS_VALUE, RESOURCE_DEUTERIUM))).getText()));
        action.setResources(resources);

        Fleet fleet = new Fleet();
        for(ShipType type: ShipType.values()){
            if(isElementExists(fleetDetails, By.xpath(String.format(FLEET_DETAILS_VALUE, ships.get(type))))){
                fleet.set(type, DataParser.parseResource(
                        getElement(fleetDetails, By.xpath(String.format(FLEET_DETAILS_VALUE, ships.get(type)))).getText()));
            }
        }
        action.setFleet(fleet);
    }

    public void openClosedFleetDetails() {
        if (isElementExists(FLEET_DETAILS) && isElementExists(CLOSED_FLEET)){
            findElements(CLOSED_FLEET).forEach(webElement -> getElement(webElement, OPEN_CLOSE_DETAILS).click());
        }
    }

    private MissionType getMissionType(WebElement element) {
        switch (element.getAttribute("data-mission-type")) {
            case "1":
                return MissionType.ATTACK;
            case "2":
                return MissionType.JOINT_ATTACK;
            case "3":
                return MissionType.TRANSPORT;
            case "4":
                return MissionType.KEEP;
            case "5":
                return MissionType.HOLD_ON;
            case "6":
                return MissionType.ESPIONAGE;
            case "7":
                return MissionType.COLONIZATION;
            case "8":
                return MissionType.RECYCLING;
            case "9":
                return MissionType.DESTROY;
            case "15":
                return MissionType.EXPEDITION;
            default:
                return MissionType.ATTACK;
        }
    }

    private boolean isReturnFlight(WebElement element) {
        return element.getAttribute("data-return-flight").equals("true");
    }

    private Coordinates getFrom(WebElement element) {
        return new Coordinates(getElement(element, By.cssSelector(".originData a")).getText());
    }

    private Coordinates getTarget(WebElement element) {
        return new Coordinates(getElement(element, By.cssSelector(".destinationData a")).getText());
    }

    private Duration getDuration(WebElement element) {
        if (isElementExists(element, By.cssSelector(".timer"))) {
            return DataParser.parseDuration(getElement(element, By.cssSelector(".timer")).getText());
        }
        return null;
    }

    private Duration getDurationReturn(WebElement element) {
        if (isElementExists(element, By.cssSelector(".nextTimer"))) {
            return DataParser.parseDuration(getElement(element, By.cssSelector(".nextTimer")).getText());
        }
        return null;
    }

    public void revertFleet(FleetAction action) {
        //TODO: add revert fleet actions
        return;
    }

    public Integer getActiveExpeditions() {
        if (isElementExists(EXPEDITION_FLEETS_COUNT)) {
            return Integer.parseInt(getElement(EXPEDITION_FLEETS_COUNT).getText());
        }
        return 0;
    }

    public Integer getActiveFleets() {
        if (isElementExists(CURRENT_FLEETS_COUNT)) {
            return Integer.parseInt(getElement(CURRENT_FLEETS_COUNT).getText());
        }
        return 0;
    }
}

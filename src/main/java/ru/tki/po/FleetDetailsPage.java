package ru.tki.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.ContextHolder;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.CheckColonyTask;
import ru.tki.models.tasks.FleetTask;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.ShipType;
import ru.tki.utils.DataParser;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FleetDetailsPage extends AbstractFleetDetails {

    private static final By CURRENT_FLEETS_COUNT        = By.cssSelector(".fleetStatus .fleetSlots .current");
    private static final By ALL_FLEETS_COUNT            = By.cssSelector(".fleetStatus .fleetSlots .all");
    private static final By EXPEDITION_FLEETS_COUNT     = By.cssSelector(".fleetStatus .expSlots .current");
    private static final By ALL_EXPEDITION_FLEETS_COUNT = By.cssSelector(".fleetStatus .expSlots .all");

    private static final By FLEET_DETAILS      = By.cssSelector(".fleetDetails");
    private static final By CLOSED_FLEET       = By.cssSelector(".fleetDetails.detailsClosed");
    private static final By OPEN_CLOSE_DETAILS = By.cssSelector(".openCloseDetails");
    private static final By FLEET_RETURN_TIMER = By.cssSelector(".nextTimer");
    private static final By FLEET_TIMER        = By.cssSelector(".timer");

    private static final String FLEET_DETAILS_VALUE = ".//tr[./td[contains(.,'%s')]]/td[@class='value']";
    private static final String FLEET_ELEMENT       = "//div[contains(@class, 'fleetDetails') and @data-return-flight='false' and contains(@data-mission-type, '%s') " +
            "and .//span[@class='originData' and contains(., '%s')] and .//span[@class='destinationData' and contains(., '%s')]]";

    public List<FleetAction> getFleetActions(Empire empire) {
        List<FleetAction> fleetActions = new ArrayList<>();
        openClosedFleetDetails();
        if (isElementExists(FLEET_DETAILS)) {
            fleetActions.addAll(findElements(FLEET_DETAILS).stream().map(fleetElement ->
                    getFleetAction(empire, fleetElement)).collect(Collectors.toList()));
        }
        return fleetActions;
    }

    private void openClosedFleetDetails() {
        if (isElementExists(FLEET_DETAILS) && isElementExists(CLOSED_FLEET)) {
            findElements(CLOSED_FLEET).forEach(webElement -> getElement(webElement, OPEN_CLOSE_DETAILS).click());
        }
    }

    public void revertFleet(FleetTask task) {
        WebElement element = getElement(getFleetElement(task), By.cssSelector(".reversal"));
        scrollToElement(element);
        element.click();
    }

    public Duration getRevertDuration(FleetAction action) {
        WebElement element = getFleetElement(action);
        return getDurationReturn(element).minus(getDuration(element).multipliedBy(2));
    }

    public Duration getRevertDuration(FleetTask fleetTask) {
        WebElement element = getFleetElement(fleetTask);
        // TODO: 30/01/2018 Revert duration is 0 for some reason. Double check it.
        return getDurationReturn(element).minus(getDuration(element).multipliedBy(2));
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

    private FleetAction getFleetAction(Empire empire, WebElement element) {
        FleetAction fleetAction = new FleetAction();
        fleetAction.setMissionType(getMissionType(element));
        if (fleetAction.getMissionType() == MissionType.EXPEDITION) {
            empire.addActiveExpedition();
        }

        Coordinates fromCoordinates = getFrom(element);
        AbstractPlanet planet = empire.findPlanet(fromCoordinates);
        if (null != planet) {
            fleetAction.setPlanet(planet);
        } else {
            fleetAction.setPlanet(new Planet(fromCoordinates));
        }

        Coordinates targetCoordinates = getTarget(element);
        planet = empire.findPlanet(targetCoordinates);
        if (null != planet) {
            fleetAction.setTargetPlanet(planet);
        } else {
            fleetAction.setTargetPlanet(new Planet(targetCoordinates));
        }
        if (isReturnFlight(element)) {
            fleetAction.setReturnFlight(true);
            fleetAction.addDuration(getDuration(element));
        } else {
            fleetAction.setDurationOfFlight(getDuration(element));
            fleetAction.addDuration(getDurationReturn(element));
        }
        if (fleetAction.getMissionType() == MissionType.COLONIZATION) {
            fleetAction.addTask(new CheckColonyTask(empire, fleetAction.getTargetPlanet(), ContextHolder.getBotConfigMain().COLONY_MIN_SIZE));
        }
        collectFleetDetails(element, fleetAction);
        return fleetAction;
    }

    private void collectFleetDetails(WebElement element, FleetAction action) {
        WebElement route = getElement(element, By.cssSelector(".route a"));
        scrollToElement(route);
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
        for (ShipType type : ShipType.values()) {
            if (isElementExists(fleetDetails, By.xpath(String.format(FLEET_DETAILS_VALUE, ships.get(type))))) {
                fleet.set(type, DataParser.parseResource(
                        getElement(fleetDetails, By.xpath(String.format(FLEET_DETAILS_VALUE, ships.get(type)))).getText()));
            }
        }
        action.setFleet(fleet);
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
        if (isElementExists(element, FLEET_TIMER)) {
            scrollToElement(getElement(element, FLEET_TIMER));
            return DataParser.parseDuration(getElement(element, FLEET_TIMER).getText());
        }
        return Duration.ZERO;
    }

    private Duration getDurationReturn(WebElement element) {
        if (isElementExists(element, FLEET_RETURN_TIMER)) {
            scrollToElement(getElement(element, FLEET_RETURN_TIMER));
            return DataParser.parseDuration(getElement(element, FLEET_RETURN_TIMER).getText());
        }
        return Duration.ZERO;
    }

    private WebElement getFleetElement(FleetTask fleetTask) {
        return getElement(By.xpath(String.format(FLEET_ELEMENT,
                getMissionTypeId(fleetTask.getMissionType()),
                fleetTask.getPlanet().getCoordinates().getFormattedCoordinates(),
                fleetTask.getTargetPlanet().getCoordinates().getFormattedCoordinates())));
    }

    private WebElement getFleetElement(FleetAction fleetAction) {
        return getElement(By.xpath(String.format(FLEET_ELEMENT,
                getMissionTypeId(fleetAction.getMissionType()),
                fleetAction.getPlanet().getCoordinates().getFormattedCoordinates(),
                fleetAction.getTargetPlanet().getCoordinates().getFormattedCoordinates())));
    }
}

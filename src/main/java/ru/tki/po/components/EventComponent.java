package ru.tki.po.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.ContextHolder;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.CheckColonyTask;
import ru.tki.models.tasks.FleetTask;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.ShipType;
import ru.tki.po.AbstractFleetDetails;
import ru.tki.utils.DataParser;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class EventComponent extends AbstractFleetDetails {

    private static final By ROOT               = By.cssSelector("#eventContent");
    private static final By OPEN_CLOSE_DETAILS = By.cssSelector("#js_eventDetailsClosed");

    private static final By     HOSTILE_FLEETS = By.xpath("//table[@id='eventContent']//tr[contains(@class, 'eventFleet') and ./td[contains(@class, 'countDown hostile')]]");
    private static final String FLEET_ELEMENT  = "//tr[contains(@class, 'eventFleet') and contains(@data-mission-type, '%s') and .//td[@class='coordsOrigin' and contains(., '%s')] and .//td[@class='destCoords' and contains(., '%s')]]";

    public List<FleetAction> getHostileFleets(Empire empire) {
        List<FleetAction> fleetActions = new ArrayList<>();
        openClosedFleetDetails();
        if (isElementExists(HOSTILE_FLEETS)) {
            fleetActions.addAll(findElements(HOSTILE_FLEETS).stream().map(fleetElement ->
                    getFleetAction(empire, fleetElement)).collect(Collectors.toList()));
        }
        return fleetActions;
    }

    private void openClosedFleetDetails() {
        if(!isElementDisplayed(ROOT)){
            waitForWebElementIsDisplayed(OPEN_CLOSE_DETAILS);
            getElement(OPEN_CLOSE_DETAILS).click();
            waitForWebElementIsDisplayed(ROOT);
        }
    }

    private FleetAction getFleetAction(Empire empire, WebElement element) {
        FleetAction fleetAction = new FleetAction();
        fleetAction.setMissionType(getMissionType(element));

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

        fleetAction.addDuration(getDuration(element));
        return fleetAction;
    }

    private boolean isReturnFlight(WebElement element) {
        return element.getAttribute("data-return-flight").equals("true");
    }

    private Coordinates getFrom(WebElement element) {
        return new Coordinates(getElement(element, By.cssSelector(".coordsOrigin a")).getText());
    }

    private Coordinates getTarget(WebElement element) {
        return new Coordinates(getElement(element, By.cssSelector(".destCoords a")).getText());
    }

    private Duration getDuration(WebElement element) {
        if (isElementExists(element, By.cssSelector(".countDown"))) {
            return DataParser.parseDuration(getElement(element, By.cssSelector(".countDown")).getText());
        }
        return Duration.ZERO;
    }

    private WebElement getFleetElement(FleetTask fleetTask) {
        return getElement(By.xpath(String.format(FLEET_ELEMENT,
                getMissionTypeId(fleetTask.getMissionType()),
                fleetTask.getPlanet().getCoordinates().getFormattedCoordinates(),
                fleetTask.getTargetPlanet().getCoordinates().getFormattedCoordinates())));
    }
}

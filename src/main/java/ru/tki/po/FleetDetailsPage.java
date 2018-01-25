package ru.tki.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.types.MissionType;
import ru.tki.utils.DataParser;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class FleetDetailsPage extends PageObject {

    private static final By CURRENT_FLEETS_COUNT        = By.cssSelector(".fleetStatus .fleetSlots .current");
    private static final By ALL_FLEETS_COUNT            = By.cssSelector(".fleetStatus .fleetSlots .all");
    private static final By EXPEDITION_FLEETS_COUNT     = By.cssSelector(".fleetStatus .fleetSlots .all");
    private static final By ALL_EXPEDITION_FLEETS_COUNT = By.cssSelector(".fleetStatus .fleetSlots .all");

    private static final By FLEET_LOCATOR = By.cssSelector(".fleetDetails");

    public List<FleetAction> getFleetActions(Empire empire){
        List<FleetAction> fleetActions = new ArrayList<>();
        if (isElementExists(FLEET_LOCATOR)){
            for(WebElement fleetElement :findElements(FLEET_LOCATOR)) {
                fleetActions.add(getFleetAction(empire, fleetElement));
            }
        }
        return fleetActions;
    }

    private FleetAction getFleetAction(Empire empire, WebElement element){
        FleetAction fleetAction = new FleetAction();
        fleetAction.setMissionType(getMissionType(element));
        AbstractPlanet planet = empire.findPlanet(getFrom(element));
        if (null != planet) {
            fleetAction.setPlanet(planet);
        }

        Coordinates targetCoordinates = getTarget(element);
        planet = empire.findPlanet(targetCoordinates);
        if (null != planet) {
            fleetAction.setTargetPlanet(planet);
        }
        else{
            fleetAction.setTargetPlanet(new Planet(targetCoordinates, ""));
        }
        if(isReturnFlight(element)) {
            Duration duration = getDuration(element);
            if (null != duration) {
                fleetAction.addDuration(duration);
            }
        }
        else {
            Duration duration = getDuration(element);
            if (null != duration) {
                fleetAction.setDurationOfFlight(duration);
            }
            duration = getDurationReturn(element);
            if (null != duration) {
                fleetAction.addDuration(duration);
            }
        }
        return fleetAction;
    }

    private MissionType getMissionType(WebElement element){
        switch (element.getAttribute("data-mission-type")){
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

    private boolean isReturnFlight(WebElement element){
        return element.getAttribute("data-return-flight").equals("true");
    }

    private Coordinates getFrom(WebElement element){
        return new Coordinates(getElement(element, By.cssSelector(".originData a")).getText());
    }

    private Coordinates getTarget(WebElement element){
        return new Coordinates(getElement(element, By.cssSelector(".destinationData a")).getText());
    }

    private Duration getDuration(WebElement element){
        if (isElementExists(element, By.cssSelector(".timer"))) {
            return DataParser.parseDuration(getElement(element, By.cssSelector(".timer")).getText());
        }
        return null;
    }

    private Duration getDurationReturn(WebElement element){
        if(isElementExists(element, By.cssSelector(".nextTimer"))) {
            return DataParser.parseDuration(getElement(element, By.cssSelector(".nextTimer")).getText());
        }
        return null;
    }

    public void revertFleet(FleetAction action){
        //TODO: add revert fleet actions
        return;
    }
}

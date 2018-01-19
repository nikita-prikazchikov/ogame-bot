package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.models.*;
import ru.tki.models.types.FleetSpeed;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.PlanetType;
import ru.tki.models.types.ShipType;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FleetPage extends PageObject {

    private static final By SELECT_ALL = By.cssSelector(".send_all");
    private static final By CONTINUE = By.cssSelector("#continue");
    private static final By GALAXY = By.cssSelector("#galaxy");
    private static final By SYSTEM = By.cssSelector("#system");
    private static final By POSITION = By.cssSelector("#position");
    private static final By DURATION = By.cssSelector("#duration");
    private static final By METAL = By.cssSelector("#metal");
    private static final By CRYSTAL = By.cssSelector("#crystal");
    private static final By DEUTERIUM = By.cssSelector("#deuterium");
    private static final By START = By.cssSelector("#start");

    private static final Map<ShipType, By> ships = new HashMap<ShipType, By>() {{
        put(ShipType.LIGHT_FIGHTER, By.cssSelector("#battleships #button204"));
        put(ShipType.HEAVY_FIGHTER, By.cssSelector("#battleships #button205"));
        put(ShipType.CRUISER, By.cssSelector("#battleships #button206"));
        put(ShipType.BATTLESHIP, By.cssSelector("#battleships #button207"));
        put(ShipType.BATTLECRUISER, By.cssSelector("#battleships #button215"));
        put(ShipType.BOMBER, By.cssSelector("#battleships #button211"));
        put(ShipType.DESTROYER, By.cssSelector("#battleships #button213"));
        put(ShipType.DEATHSTAR, By.cssSelector("#battleships #button214"));

        put(ShipType.SMALL_CARGO, By.cssSelector("#civilships #button202"));
        put(ShipType.LARGE_CARGO, By.cssSelector("#civilships #button203"));
        put(ShipType.COLONY_SHIP, By.cssSelector("#civilships #button208"));
        put(ShipType.RECYCLER, By.cssSelector("#civilships #button209"));
        put(ShipType.ESPIONAGE_PROBE, By.cssSelector("#civilships #button210"));
    }};

    private static final Map<PlanetType, By> targets = new HashMap<PlanetType, By>() {{
        put(PlanetType.PLANET, By.cssSelector("#pbutton"));
        put(PlanetType.MOON, By.cssSelector("#mbutton"));
        put(PlanetType.DEBRIS, By.cssSelector("#dbutton"));
    }};

    private static final Map<FleetSpeed, By> speed = new HashMap<FleetSpeed, By>() {{
        put(FleetSpeed.S10, By.cssSelector("a.speed[data-value='1']"));
        put(FleetSpeed.S20, By.cssSelector("a.speed[data-value='2']"));
        put(FleetSpeed.S30, By.cssSelector("a.speed[data-value='3']"));
        put(FleetSpeed.S40, By.cssSelector("a.speed[data-value='4']"));
        put(FleetSpeed.S50, By.cssSelector("a.speed[data-value='5']"));
        put(FleetSpeed.S60, By.cssSelector("a.speed[data-value='6']"));
        put(FleetSpeed.S70, By.cssSelector("a.speed[data-value='7']"));
        put(FleetSpeed.S80, By.cssSelector("a.speed[data-value='8']"));
        put(FleetSpeed.S90, By.cssSelector("a.speed[data-value='9']"));
        put(FleetSpeed.S100, By.cssSelector("a.speed[data-value='10']"));
    }};

    private static final Map<MissionType, By> missionTypes = new HashMap<MissionType, By>() {{
        put(MissionType.EXPEDITION, By.cssSelector("#missionButton15"));
        put(MissionType.COLONIZATION, By.cssSelector("#missionButton7"));
        put(MissionType.RECYCLING, By.cssSelector("#missionButton8"));
        put(MissionType.TRANSPORT, By.cssSelector("#missionButton3"));
        put(MissionType.KEEP, By.cssSelector("#missionButton4"));
        put(MissionType.ESPIONAGE, By.cssSelector("#missionButton6"));
        put(MissionType.HOLD_ON, By.cssSelector("#missionButton5"));
        put(MissionType.ATTACK, By.cssSelector("#missionButton1"));
        put(MissionType.JOINT_ATTACK, By.cssSelector("#missionButton2"));
        put(MissionType.DESTROY, By.cssSelector("#missionButton9"));
    }};

    public void selectFleet(Fleet fleet) {
        if (fleet.getLightFighter() != null) {
            selectShip(ShipType.LIGHT_FIGHTER, fleet.getLightFighter());
        }
        if (fleet.getHeavyFighter() != null) {
            selectShip(ShipType.HEAVY_FIGHTER, fleet.getHeavyFighter());
        }
        if (fleet.getCruiser() != null) {
            selectShip(ShipType.CRUISER, fleet.getCruiser());
        }
        if (fleet.getBattleship() != null) {
            selectShip(ShipType.BATTLESHIP, fleet.getBattleship());
        }
        if (fleet.getBattlecruiser() != null) {
            selectShip(ShipType.BATTLECRUISER, fleet.getBattlecruiser());
        }
        if (fleet.getBomber() != null) {
            selectShip(ShipType.BOMBER, fleet.getBomber());
        }
        if (fleet.getDestroyer() != null) {
            selectShip(ShipType.DESTROYER, fleet.getDestroyer());
        }
        if (fleet.getDeathStar() != null) {
            selectShip(ShipType.DEATHSTAR, fleet.getDeathStar());
        }
        if (fleet.getSmallCargo() != null) {
            selectShip(ShipType.SMALL_CARGO, fleet.getSmallCargo());
        }
        if (fleet.getLargeCargo() != null) {
            selectShip(ShipType.LARGE_CARGO, fleet.getLargeCargo());
        }
        if (fleet.getColonyShip() != null) {
            selectShip(ShipType.COLONY_SHIP, fleet.getColonyShip());
        }
        if (fleet.getRecycler() != null) {
            selectShip(ShipType.RECYCLER, fleet.getRecycler());
        }
        if (fleet.getEspionageProbe() != null) {
            selectShip(ShipType.ESPIONAGE_PROBE, fleet.getEspionageProbe());
        }
    }

    public void selectShip(ShipType type, int value) {
        setValue(getElement(getElement(ships.get(type)), By.cssSelector(".fleetValues")), Integer.toString(value));
    }

    public void setTarget(PlanetType type) {
        getElement(targets.get(type)).click();
    }

    public void setSpeed(FleetSpeed speed) {
        getElement(FleetPage.speed.get(speed)).click();
    }

    public void setMission(MissionType mission) {
        getElement(FleetPage.missionTypes.get(mission)).click();
    }

    public void setCoordinates(Coordinates coordinates){
        setValue(getElement(GALAXY), coordinates.getGalaxy());
        setValue(getElement(SYSTEM), coordinates.getSystem());
        setValue(getElement(POSITION), coordinates.getPlanet());
    }

    public void setResources(Resources resources){
        setValue(getElement(METAL), Integer.toString(resources.getMetal()));
        setValue(getElement(CRYSTAL), Integer.toString(resources.getCrystal()));
        setValue(getElement(DEUTERIUM), Integer.toString(resources.getDeuterium()));
    }

    public void selectAllShips(){
        getElement(SELECT_ALL).click();
    }

    public void clickContinue(){
        getElement(CONTINUE).click();
    }

    public void clickStart(){
        getElement(START).click();
    }

    public Duration getDuration(){
        Duration d = Duration.ZERO;

        String text = getElement(DURATION).getText();
        Matcher m = Pattern.compile("(\\d+):(\\d+):(\\d+)").matcher(text);
        if(m.find()){
            d = d.plusHours(Integer.parseInt(m.group(1)))
                    .plusMinutes(Integer.parseInt(m.group(2)))
                    .plusSeconds(Integer.parseInt(m.group(3)));
        }
        return d;
    }

    public int getShipCount(ShipType type) {
        return Integer.parseInt(
                getElement(getElement(ships.get(type)), By.cssSelector(".level")).getText().trim());
    }

    public Fleet getFleet() {
        Fleet fleet = new Fleet();
        for (ShipType type : ShipType.values()) {
            if (type == ShipType.SOLAR_SATELLITE)
                continue;
            fleet.set(type, getShipCount(type));
        }
        return fleet;
    }
}

package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.ContextHolder;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.*;
import ru.tki.models.types.FactoryType;
import ru.tki.utils.DataParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OverviewPage extends PageObject {

    private static final By PLANET_NAME       = By.cssSelector("#planetNameHeader");
    private static final By PLANET_SIZE       = By.cssSelector("#diameterContentField");
    private static final By OVERVIEW_ELEMENTS = By.cssSelector("#overviewBottom .content");
    private static final By IDLE              = By.cssSelector(".idle");
    private static final By BUILDING_DURATION = By.cssSelector("#Countdown");
    private static final By RESEARCH_DURATION = By.cssSelector("#researchCountdown");
    private static final By SHIPYARD_DURATION = By.cssSelector(".shipAllCountdown");

    private static final By PLANET_MOVE_RENAME_LINK = By.cssSelector(".planetMoveOverviewGivUpLink");
    private static final By PLANET_NAME_INPUT       = By.cssSelector("#planetName");
    private static final By PLANET_RENAME_BUTTON    = By.cssSelector("#planetMaintenance .btn_blue");
    private static final By LEAVE_COLONY_BUTTON     = By.cssSelector("#block");
    private static final By LEAVE_PASSWORD_FIELD    = By.cssSelector(".pw_field");
    private static final By CONFIRM_LEAVE_BUTTON    = By.cssSelector("#validate .btn_blue");

    private static final By BUILDING_TITLE = By.xpath("//div[@id='overviewBottom']//div[@class='content' and .//span[@id='Countdown']]//th");

    public String getPlanetName() {
        waitForWebElement(PLANET_NAME);
        return getElement(PLANET_NAME).getText();
    }

    public Integer getPlanetSize() {
        waitForWebElementStopMoving(PLANET_SIZE);
        Matcher m = Pattern.compile("\\d+/(\\d+)").matcher(getElement(PLANET_SIZE).getText());
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    public Action getBuildAction(Empire empire, AbstractPlanet planet) {
        Action action;
        if (isElementExists(BUILDING_TITLE)) {
            String title = getElement(BUILDING_TITLE).getText().trim().toLowerCase();
            if (title.contains("верфь")) {
                action = new FactoryAction(planet, FactoryType.SHIPYARD);
                planet.setShipyardBusy(true);
            } else if (title.contains("нанитов")) {
                action = new FactoryAction(planet, FactoryType.NANITE_FACTORY);
                planet.setShipyardBusy(true);

            } else if (title.contains("лаборатория")) {
                action = new FactoryAction(planet, FactoryType.RESEARCH_LAB);
                planet.setShipyardBusy(true);
                empire.setResearchInProgress(true);

            } else {
                action = new BuildingAction(planet);
            }
            if (isElementExists(BUILDING_DURATION)) {
                action.addDuration(DataParser.parseDuration(getElement(BUILDING_DURATION).getText()));
                planet.setBuildInProgress(true);
                return action;
            }
        }
        planet.setBuildInProgress(false);
        return null;
    }

    public Action getResearchAction(Empire empire, AbstractPlanet planet) {
        ResearchAction researchAction = new ResearchAction(planet);
        if (isElementExists(RESEARCH_DURATION)) {
            researchAction.addDuration(DataParser.parseDuration(getElement(RESEARCH_DURATION).getText()));
            empire.setResearchInProgress(true);
            return researchAction;
        }
        empire.setResearchInProgress(false);
        return null;
    }

    public Action getShipyardAction(AbstractPlanet planet) {
        ShipyardAction shipyardAction = new ShipyardAction(planet);
        if (isElementExists(SHIPYARD_DURATION)) {
            shipyardAction.addDuration(DataParser.parseDuration(getElement(SHIPYARD_DURATION).getText()));
            planet.setShipyardBusy(true);
            return shipyardAction;
        }
        planet.setShipyardBusy(false);
        return null;
    }

    public void renamePlanet(String name) {
        getElement(PLANET_MOVE_RENAME_LINK).click();
        waitForWebElementIsDisplayed(PLANET_NAME_INPUT);
        setValue(getElement(PLANET_NAME_INPUT), name);
        getElement(PLANET_RENAME_BUTTON).click();
    }

    public void leavePlanet() {
        getElement(PLANET_MOVE_RENAME_LINK).click();
        waitForWebElementIsDisplayed(LEAVE_COLONY_BUTTON);
        getElement(LEAVE_COLONY_BUTTON).click();
        waitForWebElementIsDisplayed(LEAVE_PASSWORD_FIELD);
        setValue(getElement(LEAVE_PASSWORD_FIELD), ContextHolder.getBotConfigMain().PASSWORD);
        getElement(CONFIRM_LEAVE_BUTTON).click();
    }
}

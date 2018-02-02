package ru.tki.po;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Coordinates;
import ru.tki.models.Planet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GalaxyPage extends PageObject {

    private static final By GALAXY_NUMBER             = By.cssSelector("#galaxy_input");
    private static final By SYSTEM_NUMBER             = By.cssSelector("#system_input");
    private static final By OPEN_BUTTON               = By.cssSelector("div.btn_blue");
    private static final By GALAXY_LOADING            = By.cssSelector("#galaxyLoading");
    private static final By NEXT_ITEM                 = By.cssSelector(".galaxy_icons.next");
    private static final By PREVIOUS_ITEM             = By.cssSelector(".galaxy_icons.prev");
    private static final By GALAXY_TABLE_ROW          = By.cssSelector("#galaxytable tr.row");
    private static final By GALAXY_TABLE_INACTIVE_ROW = By.cssSelector("#galaxytable tr.row.inactive_filter");
    private static final By GALAXY_TABLE_EMPTY_ROW    = By.cssSelector("#galaxytable tr.row.empty_filter");

    private static final By POSITION = By.cssSelector(".position");

    public void findPlanet(AbstractPlanet planet) {
        this.findPlanet(planet.getCoordinates());
    }

    public void findPlanet(Coordinates coordinates) {
        setValue(getElement(GALAXY_NUMBER), coordinates.getGalaxy());
        setValue(getElement(SYSTEM_NUMBER), coordinates.getSystem().toString() + Keys.ENTER);
        waitLoading();
    }

    public void selectSystem(Integer value) {
        setValue(getElement(SYSTEM_NUMBER), value.toString() + Keys.ENTER);
        waitLoading();
    }

    public void selectGalaxy(Integer value) {
        setValue(getElement(GALAXY_NUMBER), value.toString() + Keys.ENTER);
        waitLoading();
    }

    public void nextSystem() {
        findElements(NEXT_ITEM).get(1).click();
        waitLoading();
    }

    public void prevSystem() {
        findElements(PREVIOUS_ITEM).get(1).click();
        waitLoading();
    }

    public boolean isEmpty(AbstractPlanet planet) {
        return isEmpty(planet.getCoordinates().getPlanet());
    }

    public boolean isEmpty(String planet) {
        return this.isEmpty(Integer.parseInt(planet));
    }

    public boolean isEmpty(int planet) {
        return findElements(GALAXY_TABLE_ROW).get(planet).getAttribute("class").contains("empty_filter");
    }

    public String getGalaxy() {
        return getElement(GALAXY_NUMBER).getAttribute("value");
    }

    public String getSystem() {
        return getElement(SYSTEM_NUMBER).getAttribute("value");
    }

    public List<Planet> getEmptyPlanets() {
        List<Planet> planets = new ArrayList<>();
        planets.addAll(findElements(GALAXY_TABLE_EMPTY_ROW).stream().map(this::getPlanet).collect(Collectors.toList()));
        return planets;
    }

    public List<Planet> getInactivePlanets() {
        List<Planet> planets = new ArrayList<>();
        planets.addAll(findElements(GALAXY_TABLE_INACTIVE_ROW).stream().map(this::getPlanet).collect(Collectors.toList()));
        return planets;
    }

    private Planet getPlanet(WebElement element) {
        return new Planet(String.format("%s:%s:%s", getGalaxy(), getSystem(), getElement(element, POSITION).getText().trim()));
    }

    private void waitLoading() {
        waitForWebElementIsDisplayed(GALAXY_LOADING, 1);
        waitForWebElementNotDisplayed(GALAXY_LOADING, 1);
    }
}

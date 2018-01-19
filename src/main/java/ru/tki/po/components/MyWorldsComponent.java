package ru.tki.po.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.models.*;
import ru.tki.po.PageObject;

import java.util.ArrayList;
import java.util.List;

public class MyWorldsComponent extends PageObject {

    private final static String PLANETS = "#planetList .smallplanet";
    private final static String PLANET_XPATH = "//div[contains(@class, 'smallplanet')]/a[./span[contains(., '%s')]]";
    private final static String MOON_XPATH = "//div[contains(@class, 'smallplanet') and .//span[contains(@class, 'planet-koords ') and contains(., '%s')]]/a[contains(@class, 'moonlink')]";

    public void selectPlanet(AbstractPlanet planet){
        switch (planet.getType()){
            case PLANET:
                selectPlanet(planet.getCoordinates());
                break;
            case MOON:
                selectMoon(planet.getCoordinates());
                break;
        }
    }

    private void selectPlanet(Coordinates coordinates){
        getElement(By.xpath(String.format(PLANET_XPATH, coordinates.getFormattedCoordinates()))).click();
    }

    private void selectMoon(Coordinates coordinates){
        getElement(By.xpath(String.format(MOON_XPATH, coordinates.getFormattedCoordinates()))).click();
    }

    public List<AbstractPlanet> getPlanets(){
        ArrayList<AbstractPlanet> planetList = new ArrayList<>();

        List<WebElement> planets = findElements(By.cssSelector(PLANETS));
        for(WebElement planetElement : planets){
            String coords = planetElement.findElement(By.cssSelector(".planet-koords ")).getText();
            String name = planetElement.findElement(By.cssSelector(".planet-name ")).getText();
            Planet planet = new Planet(coords, name);
            planetList.add(planet);

            if(isElementExists(planetElement, By.cssSelector(".moonlink"))){
                Moon moon = new Moon(coords);
                planetList.add(moon);
            }
        }
        return planetList;
    }
}

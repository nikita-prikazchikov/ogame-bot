package ru.tki.po.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.models.Coordinates;
import ru.tki.models.IPlanet;
import ru.tki.models.Moon;
import ru.tki.models.Planet;
import ru.tki.po.PageObject;

import java.util.ArrayList;
import java.util.List;

public class MyWorlds extends PageObject {

    private final static String PLANETS = "#planetList .smallplanet";
    private final static String PLANET_XPATH = "//div[contains(@class, 'smallplanet')]/a[./span[contains(., '%s')]]";
    private final static String MOON_XPATH = "//div[contains(@class, 'smallplanet') and .//span[contains(@class, 'planet-koords ') and contains(., '%s')]]/a[contains(@class, 'moonlink')]";

    public void selectPlanet(IPlanet planet){
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
        this.driver.findElement(By.xpath(String.format(PLANET_XPATH, coordinates.getFormattedCoordinates()))).click();
    }

    private void selectMoon(Coordinates coordinates){
        this.driver.findElement(By.xpath(String.format(MOON_XPATH, coordinates.getFormattedCoordinates()))).click();
    }

    public List<IPlanet> getPlanets(){
        ArrayList<IPlanet> planetList = new ArrayList<IPlanet>();

        List<WebElement> planets = webDriverHelper.findElements(By.cssSelector(PLANETS));
        for(WebElement planetElement : planets){
            String coords = planetElement.findElement(By.cssSelector(".planet-koords ")).getText();
            String name = planetElement.findElement(By.cssSelector(".planet-name ")).getText();
            Planet planet = new Planet(coords, name);
            planetList.add(planet);

            if(webDriverHelper.isElementExists(planetElement, By.cssSelector(".moonlink"))){
                Moon moon = new Moon(coords);
                planetList.add(moon);
            }
        }
        return planetList;
    }
}

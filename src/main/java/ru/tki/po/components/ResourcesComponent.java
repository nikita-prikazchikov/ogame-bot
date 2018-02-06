package ru.tki.po.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.tki.models.Resources;
import ru.tki.po.PageObject;
import ru.tki.utils.DataParser;

public class ResourcesComponent extends PageObject {

    @FindBy(id="resources_metal")
    WebElement metal;

    @FindBy(id="resources_crystal")
    WebElement crystal;

    @FindBy(id="resources_deuterium")
    WebElement deuterium;

    @FindBy(id="resources_energy")
    WebElement energy;

    @FindBy(id="resources_darkmatter")
    WebElement resources_darkmatter;

    public int getMetal() {
        return DataParser.parseResource(metal.getText());
    }

    public int getCrystal() {
        return DataParser.parseResource(crystal.getText());
    }

    public int getDeuterium() {
        return DataParser.parseResource(deuterium.getText());
    }

    public int getEnergy() {
        return DataParser.parseResource(energy.getText());
    }

    public int getDarkMatter() {
        return DataParser.parseResource(resources_darkmatter.getText());
    }

    public Resources getResources(){
        waitForWebElementIsDisplayed(By.cssSelector("#resources_metal"));
        return new Resources().setMetal(getMetal())
                .setCrystal(getCrystal())
                .setDeuterium(getDeuterium())
                .setEnergy(getEnergy());
    }
}

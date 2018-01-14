package ru.tki.po.components;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.tki.po.PageObject;
import ru.tki.utils.DataParser;

public class Resources extends PageObject {

    @FindBy(id="resources_metal")
    WebElement metal;

    @FindBy(id="resources_crystal")
    WebElement crystal;

    @FindBy(id="resources_deuterium")
    WebElement deuterium;

    @FindBy(id="resources_energy")
    WebElement energy;

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
}

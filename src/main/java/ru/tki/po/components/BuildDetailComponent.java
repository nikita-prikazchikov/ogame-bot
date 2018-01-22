package ru.tki.po.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.tki.models.Resources;
import ru.tki.po.PageObject;
import ru.tki.utils.DataParser;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildDetailComponent extends PageObject {

    private static final String ROOT = "#detail";
    private static final By LEVEL = By.cssSelector(ROOT + " .level");
    private static final By DURATION = By.cssSelector(ROOT + " #buildDuration");
    private static final By METAL = By.cssSelector(ROOT + " #costs .metal .cost");
    private static final By CRYSTAL = By.cssSelector(ROOT + " #costs .crystal .cost");
    private static final By DEUTERIUM = By.cssSelector(ROOT + " #costs .deuterium .cost");
    private static final By AMOUNT = By.cssSelector(ROOT + "  #number");
    private static final By BUILD_IT = By.cssSelector(ROOT + " .build-it");

    public int getLevel(){
        Matcher m = Pattern.compile("(/d+)").matcher(getElement(LEVEL).getText());
        if(m.find()){
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }

    public Duration getDuration(){
        return DataParser.parseDuration(getElement(DURATION).getText());
    }

    public Resources getResources(){
        Resources r = new Resources();

        if(isElementExists(METAL)){
            r.setMetal(DataParser.parseResource(getElement(METAL).getText()));
        }
        if(isElementExists(CRYSTAL)){
            r.setCrystal(DataParser.parseResource(getElement(CRYSTAL).getText()));
        }
        if(isElementExists(DEUTERIUM)){
            r.setDeuterium(DataParser.parseResource(getElement(DEUTERIUM).getText()));
        }
        return r;
    }

    public void setAmount(int value){
        this.setAmount(Integer.toString(value));
    }

    public void setAmount(String value){
        WebElement element = getElement(AMOUNT);
        element.clear();
        element.sendKeys(value);
    }

    public void build(){
        getElement(BUILD_IT).click();
    }
}

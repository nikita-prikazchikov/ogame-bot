package ru.tki.po;

import org.openqa.selenium.By;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OverviewPage extends PageObject {

    private static final By PLANET_NAME = By.cssSelector("#planetNameHeader");
    private static final By PLANET_SIZE = By.cssSelector("#diameterContentField");

    public String getPlanetName(){
        return getElement(PLANET_NAME).getText();
    }

    public Integer getPlanetSize(){
        Matcher m = Pattern.compile("\\d+/(\\d+)").matcher(getElement(PLANET_SIZE).getText());
        if(m.find()){
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
}

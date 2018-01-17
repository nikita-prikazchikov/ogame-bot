package ru.tki.po.components;

import org.openqa.selenium.By;
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
    private static final By BUILD_IT = By.cssSelector(ROOT + " #costs .build-it");

    public int getLevel(){
        Matcher m = Pattern.compile("(/d+)").matcher(helper.findElement(LEVEL).getText());
        if(m.find()){
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }

    public Duration getDuration(){
        Duration d = Duration.ZERO;

        String text = helper.findElement(DURATION).getText();
        Matcher m = Pattern.compile("(/d+)с").matcher(text);
        if(m.find()){
            d = d.plusSeconds(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(/d+)м").matcher(text);
        if(m.find()){
            d = d.plusMinutes(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(/d+)ч").matcher(text);
        if(m.find()){
            d = d.plusHours(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(/d+)д").matcher(text);
        if(m.find()){
            d = d.plusDays(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(/d+)нед").matcher(text);
        if(m.find()){
            d = d.plusDays(Integer.parseInt(m.group(1)) * 7);
        }
        return d;
    }

    public Resources getResources(){
        Resources r = new Resources();

        if(helper.isElementExists(METAL)){
            r.setMetal(DataParser.parseResource(helper.findElement(METAL).getText()));
        }
        if(helper.isElementExists(CRYSTAL)){
            r.setCrystal(DataParser.parseResource(helper.findElement(CRYSTAL).getText()));
        }
        if(helper.isElementExists(DEUTERIUM)){
            r.setDeuterium(DataParser.parseResource(helper.findElement(DEUTERIUM).getText()));
        }
        return r;
    }

    public void build(){
        helper.findElement(BUILD_IT).click();
    }
}

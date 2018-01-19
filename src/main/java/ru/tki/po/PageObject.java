package ru.tki.po;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import ru.tki.ContextHolder;
import ru.tki.helpers.WebDriverHelper;


public class PageObject extends WebDriverHelper{
    protected WebDriver driver;

    public PageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public PageObject(){
        this(ContextHolder.getDriver());
    }
}

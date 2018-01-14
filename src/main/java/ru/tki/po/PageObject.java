package ru.tki.po;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import ru.tki.ContextHolder;
import ru.tki.helpers.WebDriverHelper;


public class PageObject {
    protected WebDriver driver;
    protected WebDriverHelper webDriverHelper = new WebDriverHelper();

    public PageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public PageObject(){
        this(ContextHolder.getDriver());
    }
}

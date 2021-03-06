package ru.tki.helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.tki.ContextHolder;
import ru.tki.DriverManager;

import java.util.ArrayList;
import java.util.List;

public class WebDriverHelper {

    private static final String ELEMENT_NOT_FOUND = "Element not found using locator: %s";

    public void pause() {
        this.pause(300);
    }

    public void pause(Integer milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendKeysSlow(WebElement element, String value) {
        for (int i = 0; i < value.length(); i++) {
            element.sendKeys(String.valueOf(value.charAt(i)));
        }
    }

    public void setValue(WebElement element, String value){
        element.clear();
        element.sendKeys(value);
    }

    public WebElement getElement(SearchContext parent, By bySelector) {
        WebElement element = findElement(parent, bySelector);
        if (element == null){
            throw new InvalidArgumentException(String.format(ELEMENT_NOT_FOUND, bySelector));
        }
        return element;
    }

    public WebElement getElement(By bySelector) {
        return this.getElement(ContextHolder.getDriver(), bySelector);
    }

    public WebElement findElement(SearchContext parent, By bySelector) {
        WebElement element = null;
        if (parent != null) {
            try {
                element = parent.findElement(bySelector);
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            } catch (Exception ex) {
                element = null;
                ex.printStackTrace();
            }
        }
        return element;
    }

    public WebElement findElement(By bySelector) {
        return this.findElement(ContextHolder.getDriver(), bySelector);
    }

    public List<WebElement> findElements(SearchContext parent, By bySelector) {
        List<WebElement> elements = new ArrayList<WebElement>();
        if (parent != null) {
            try {
                elements = parent.findElements(bySelector);
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
            } catch (Exception ex) {
                elements = null;
                ex.printStackTrace();
            }
        }
        return elements;
    }

    public List<WebElement> findElements(By bySelector) {
        return this.findElements(ContextHolder.getDriver(), bySelector);
    }

    public boolean isElementExists(SearchContext parent, By bySelector) {
        try {
            ContextHolder.getDriverManager().resetImplicitlyWait();
            return this.findElement(parent, bySelector) != null;
        }
        catch (Exception ignored){
            return false;
        }
        finally {
            ContextHolder.getDriverManager().setImplicitlyWait();
        }
    }

    public boolean isElementExists(By bySelector) {
        return this.isElementExists(ContextHolder.getDriver(), bySelector);
    }

    public WebElement waitForWebElement(final SearchContext parent, final By bySelector){
        WebElement element;
        try{
            final WebDriverHelper that = this;
            element = (new WebDriverWait(ContextHolder.getDriver(), DriverManager.getImplicitlyWait()))
                    .until(d -> that.findElement(parent, bySelector));
        } catch (TimeoutException ex){
            element = null;
        }
        return element;
    }

    public WebElement waitForWebElement(final By bySelector) {
        return this.waitForWebElement(ContextHolder.getDriver(), bySelector);
    }
}

package ru.tki.helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
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

    protected void sendKeysSlow(WebElement element, String value) {
        for (int i = 0; i < value.length(); i++) {
            element.sendKeys(String.valueOf(value.charAt(i)));
        }
    }

    protected void setValue(WebElement element, String value){
        element.clear();
        element.sendKeys(value);
    }

    protected WebElement getElement(SearchContext parent, By bySelector) {
        WebElement element = findElement(parent, bySelector);
        if (element == null){
            throw new InvalidArgumentException(String.format(ELEMENT_NOT_FOUND, bySelector));
        }
        return element;
    }

    protected WebElement getElement(By bySelector) {
        return this.getElement(ContextHolder.getDriver(), bySelector);
    }

    protected WebElement findElement(SearchContext parent, By bySelector) {
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

    protected WebElement findElement(By bySelector) {
        return this.findElement(ContextHolder.getDriver(), bySelector);
    }

    protected List<WebElement> findElements(SearchContext parent, By bySelector) {
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

    protected List<WebElement> findElements(By bySelector) {
        return this.findElements(ContextHolder.getDriver(), bySelector);
    }

    protected boolean isElementExists(SearchContext parent, By bySelector) {
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

    protected boolean isElementExists(By bySelector) {
        return this.isElementExists(ContextHolder.getDriver(), bySelector);
    }

    protected boolean isElementDisplayed(WebElement element){
        boolean displayed = false;
        if(element != null){
            try{
                displayed = element.isDisplayed();
            } catch(NoSuchElementException | StaleElementReferenceException ex){
                displayed = false;
            }
        }
        return displayed;
    }

    protected boolean isElementDisplayed(SearchContext parent, By bySelector){
        try {
            ContextHolder.getDriverManager().resetImplicitlyWait();
            return this.isElementDisplayed( this.findElement(parent, bySelector) );
        }
        catch (Exception ignored){
            return false;
        }
        finally {
            ContextHolder.getDriverManager().setImplicitlyWait();
        }
    }

    protected boolean isElementDisplayed(By bySelector){
        return isElementDisplayed(ContextHolder.getDriver(), bySelector);
    }

    protected void hoverElement(WebElement element) {
        Actions actions = new Actions(ContextHolder.getDriver());
        actions.moveToElement(element).perform();
    }

    protected void hoverElement(SearchContext parent, By by){
        hoverElement(getElement(parent, by));
    }

    protected void hoverElement(By by) {
        hoverElement(ContextHolder.getDriver(), by);
    }

    //=====================    Wait elements section   ==================================

    protected WebElement waitForWebElement(final SearchContext parent, final By bySelector){
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

    protected WebElement waitForWebElement(final By bySelector) {
        return this.waitForWebElement(ContextHolder.getDriver(), bySelector);
    }

    protected boolean waitForWebElementNotExist(final SearchContext parent, final By bySelector, Integer timeout){
        boolean result = false;
        try{
            final WebDriverHelper that = this;
            ContextHolder.getDriverManager().resetImplicitlyWait();
            result = (new WebDriverWait(ContextHolder.getDriver(), timeout))
                    .until(d -> !that.isElementExists(parent, bySelector));
        } catch (TimeoutException ex) {
            result = false;
        }
        finally {
            ContextHolder.getDriverManager().setImplicitlyWait();
        }
        return result;
    }

    protected boolean waitForWebElementNotExist(final SearchContext parent, final By bySelector) {
        return waitForWebElementNotExist(parent, bySelector, DriverManager.getImplicitlyWait());
    }

    protected boolean waitForWebElementNotExist(final By bySelector, Integer timeout) {
        return waitForWebElementNotExist(ContextHolder.getDriver(), bySelector, timeout);
    }

    protected boolean waitForWebElementNotExist(final By bySelector){
        return this.waitForWebElementNotExist(ContextHolder.getDriver(), bySelector, DriverManager.getImplicitlyWait());
    }

    protected Boolean waitForWebElementIsDisplayed(final SearchContext parent, final By bySelector, Integer timeout){
        Boolean result = false;
        try{
            final WebDriverHelper that = this;
            ContextHolder.getDriverManager().resetImplicitlyWait();
            result = (new WebDriverWait(ContextHolder.getDriver(), timeout))
                    .until(d -> that.isElementDisplayed(parent, bySelector));
        } catch (TimeoutException ex){
            result = false;
        }
        finally {
            ContextHolder.getDriverManager().setImplicitlyWait();
        }
        return result;
    }

    protected Boolean waitForWebElementIsDisplayed(final SearchContext parent, final By bySelector) {
        return waitForWebElementIsDisplayed(parent, bySelector, DriverManager.getImplicitlyWait());
    }

    protected Boolean waitForWebElementIsDisplayed(final By bySelector, Integer timeout) {
        return waitForWebElementIsDisplayed(ContextHolder.getDriver(), bySelector, timeout);
    }

    protected Boolean waitForWebElementIsDisplayed(final By bySelector){
        return this.waitForWebElementIsDisplayed(ContextHolder.getDriver(), bySelector, DriverManager.getImplicitlyWait());
    }

    protected Boolean waitForWebElementNotDisplayed(final SearchContext parent, final By bySelector, Integer timeout){
        Boolean result = false;
        try{
            final WebDriverHelper that = this;
            ContextHolder.getDriverManager().resetImplicitlyWait();
            result = (new WebDriverWait(ContextHolder.getDriver(), timeout))
                    .until(d -> !that.isElementDisplayed(parent, bySelector));
        } catch (TimeoutException ex){
            result = false;
        }
        finally {
            ContextHolder.getDriverManager().setImplicitlyWait();
        }
        return result;
    }

    protected Boolean waitForWebElementNotDisplayed(final SearchContext parent, final By bySelector) {
        return waitForWebElementNotDisplayed(parent, bySelector, DriverManager.getImplicitlyWait());
    }

    protected Boolean waitForWebElementNotDisplayed(final By bySelector, Integer timeout) {
        return waitForWebElementNotDisplayed(ContextHolder.getDriver(), bySelector, timeout);
    }

    protected Boolean waitForWebElementNotDisplayed(final By bySelector){
        return this.waitForWebElementNotDisplayed(ContextHolder.getDriver(), bySelector, DriverManager.getImplicitlyWait());
    }

    protected Boolean waitForWebElementStopMoving(final WebElement element){
        Boolean result;
        try{
            final WebDriverHelper that = this;
            result = (new WebDriverWait(ContextHolder.getDriver(), DriverManager.getImplicitlyWait()))
                    .until(d -> {
                        Point p = element.getLocation();
                        that.pause(200); // wait for one second
                        Point c = element.getLocation();
                        return (p.getX() == c.getX()) && (p.getY() == c.getY());
                    });
        } catch (TimeoutException ex){
            result = false;
        }
        return result;
    }

    protected Boolean waitForWebElementStopMoving(By bySelector) {
        return waitForWebElementStopMoving(getElement(bySelector));
    }

    protected Boolean waitForWebElementStopMoving(final SearchContext parent, final By bySelector) {
        return waitForWebElementStopMoving(getElement(parent, bySelector));
    }
}

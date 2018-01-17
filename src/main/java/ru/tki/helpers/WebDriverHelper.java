package ru.tki.helpers;

import org.openqa.selenium.*;
import ru.tki.ContextHolder;

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
}

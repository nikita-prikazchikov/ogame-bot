package ru.tki.helpers;

import org.openqa.selenium.*;
import ru.tki.ContextHolder;

import java.util.ArrayList;
import java.util.List;

public class WebDriverHelper {

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

    public WebElement findElement(SearchContext parent, By bySelector) {
        WebElement element = null;
        if (parent != null) {
            try {
                element = parent.findElement(bySelector);
            } catch (NoSuchElementException ignored) {
            } catch (StaleElementReferenceException ignored) {
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
            } catch (NoSuchElementException ignored) {
            } catch (StaleElementReferenceException ignored) {
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
        ContextHolder.getDriverManager().resetImplicitlyWait();
        boolean r = this.findElement(parent, bySelector) != null;
        ContextHolder.getDriverManager().setImplicitlyWait();
        return r;
    }

    public boolean isElementExists(By bySelector) {
        boolean r = false;
        try {
            ContextHolder.getDriverManager().resetImplicitlyWait();
            r = (this.findElement(ContextHolder.getDriver(), bySelector) != null);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ContextHolder.getDriverManager().setImplicitlyWait();
        }
        return r;
    }
}

package ru.tki.po;

import org.openqa.selenium.By;
import ru.tki.utils.DataParser;


public class TraderPage extends PageObject {

    private static final By IMPORT_EXPORT       = By.cssSelector("#js_traderImportExport");
    private static final By IMPORT_PRICE        = By.cssSelector(".js_import_price");
    private static final By IMPORT_MAX_METAL    = By.cssSelector(".js_sliderMetalMax");
    private static final By IMPORT_DIV          = By.cssSelector("#div_traderImportExport");
    private static final By IMPORT_PAY          = By.cssSelector(".pay");
    private static final By IMPORT_PAY_DISABLED = By.cssSelector(".pay.disabled");
    private static final By IMPORT_GET_BONUS    = By.cssSelector(".bargain.import_bargain.take");

    public void selectImportExport() {
        waitForWebElementIsDisplayed(IMPORT_EXPORT);
        getElement(IMPORT_EXPORT).click();
    }

    private void clickMaxMetal() {
        waitForWebElementStopMoving(IMPORT_MAX_METAL);
        scrollToElement(getElement(IMPORT_MAX_METAL));
        getElement(IMPORT_MAX_METAL).click();
    }

    public void payImportExport() {
        waitForWebElementNotExist(IMPORT_PAY_DISABLED);
        getElement(IMPORT_PAY).click();
    }

    private void takeImportBonusButtonClick() {
        waitForWebElementIsDisplayed(IMPORT_GET_BONUS);
        scrollToElement(getElement(IMPORT_GET_BONUS));
        getElement(IMPORT_GET_BONUS).click();
    }

    public Integer getCost() {
        return DataParser.parseResource(getElement(IMPORT_PRICE).getText());
    }

    public void payWithMetal() {
        waitForWebElementStopMoving(getElement(IMPORT_DIV));
        if (isElementDisplayed(IMPORT_MAX_METAL)) {
            clickMaxMetal();
        }
        if (isElementDisplayed(IMPORT_PAY)) {
            payImportExport();
            pause();
        }
        if(isElementExists(IMPORT_GET_BONUS) && isElementDisplayed(IMPORT_GET_BONUS)){
            waitForWebElementStopMoving(IMPORT_GET_BONUS);
            scrollToElement(getElement(IMPORT_GET_BONUS));
            getElement(IMPORT_GET_BONUS).click();
        }
    }
}

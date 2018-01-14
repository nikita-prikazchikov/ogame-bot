package ru.tki.helpers;

import org.openqa.selenium.WebElement;

public class WebDriverHelper {

    public void pause(){
        this.pause(300);
    }

    public void pause(Integer milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendKeysSlow(WebElement element, String value){
        for (int i = 0; i < value.length(); i++) {
            element.sendKeys(String.valueOf(value.charAt(i)));
        }
    }
}

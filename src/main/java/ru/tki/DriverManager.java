package ru.tki;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DriverManager {

    public static final int IMPLICITLY_WAIT = 10;

    private static Logger logger = Logger.getLogger(String.valueOf(DriverManager.class));
    private WebDriver driver = null;

    private void startDriver(){
        this.driver = new ChromeDriver();
    }

    public WebDriver getDriver(){
        if(this.driver == null){
            this.startDriver();
            this.setImplicitlyWait();
            this.maximize();
        }
        return this.driver;
    }

    public void maximize (){
        this.driver.manage().window().maximize();
    }

    public void closeDriver(){
        this.driver.close();
        this.driver = null;
    }

    public void setImplicitlyWait(int seconds){
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }

    public void setImplicitlyWait(){
        this.setImplicitlyWait(IMPLICITLY_WAIT);
    }

    public void resetImplicitlyWait(){
        this.setImplicitlyWait(0);
    }
}

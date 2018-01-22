package ru.tki.po;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import ru.tki.BotConfigMain;
import ru.tki.ContextHolder;


public class LoginPage extends PageObject {

    private final static By OVERLAY = By.cssSelector(".openX_int_closeButton");

    @FindBy(id="loginBtn")
    WebElement login;

    @FindBy(id="loginForm")
    WebElement loginForm;

    @FindBy(id="usernameLogin")
    WebElement username;

    @FindBy(id="passwordLogin")
    WebElement password;

    @FindBy(id="serverLogin")
    WebElement universe;

    @FindBy(id="loginSubmit")
    WebElement loginSubmit;

    @FindBy(css = ".OGameClock")
    WebElement clock;

    public void login(){
        BotConfigMain conf = ContextHolder.getBotConfigMain();
        if(isElementExists(OVERLAY)){
            getElement(getElement(OVERLAY), By.cssSelector("a")).click();
        }

        openLoginForm();
        login(conf.getLogin(), conf.getPassword(), conf.getUniverse());
    }

    public void checkLogin(){
        if (!isLoggedIn()){
            login();
        }
    }

    public void login(String login, String password, String universe){
        this.username.clear();
        sendKeysSlow(this.username, login);
        this.password.clear();
        sendKeysSlow(this.password, password);
        //Click Tab here to avoid password protection popups blocking login click
        this.password.sendKeys(Keys.TAB);

        new Select(this.universe).selectByVisibleText(universe);

        this.loginSubmit.click();
    }

    public void openLoginForm(){
        if(!this.loginForm.isDisplayed()){
            this.login.click();
            pause();
        }
    }

    public boolean isLoggedIn(){
        try {
            ContextHolder.getDriverManager().setImplicitlyWait(0);
            this.clock.getText();
            return true;
        }
        catch (NoSuchElementException e){
            return false;
        }
        finally {
            ContextHolder.getDriverManager().setImplicitlyWait();
        }
    }
}

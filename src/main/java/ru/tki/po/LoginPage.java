package ru.tki.po;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import ru.tki.BotConfigMain;
import ru.tki.ContextHolder;


public class LoginPage extends PageObject {

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

    @FindBy(id="usernameLogin")
    WebElement loginSubmit;

    public void login(){
        BotConfigMain conf = ContextHolder.getBotConfigMain();

        openLoginForm();
        login(conf.getLogin(), conf.getPassword(), conf.getUniverse());
    }

    public void login(String login, String password, String universe){
        this.username.clear();
        webDriverHelper.sendKeysSlow(this.username, login);
        this.password.clear();
        webDriverHelper.sendKeysSlow(this.password, password);

        new Select(this.universe).selectByVisibleText(universe);

        this.loginSubmit.click();
    }

    public void openLoginForm(){
        if(!this.loginForm.isDisplayed()){
            this.login.click();
        }
    }
}

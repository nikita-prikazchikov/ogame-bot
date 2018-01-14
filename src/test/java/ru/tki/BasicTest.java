package ru.tki;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.tki.executor.Navigation;
import ru.tki.po.LoginPage;

public class BasicTest {

    DriverManager driverManager;
    BotConfigMain config;

    @Before
    public void setUp() throws Exception {
        BotConfigMainReader reader = new BotConfigMainReader();
        config = reader.getPropValues();
        ContextHolder.setBotConfigMain(config);

        driverManager = new DriverManager();
        ContextHolder.setDriverManager(driverManager);
    }

    @After
    public void tearDown() throws Exception {
        driverManager.closeDriver();
    }

    @Test
    public void testLogin() throws Exception {
        Navigation navigation = new Navigation();
        navigation.openHomePage();

        LoginPage loginPage = new LoginPage();
        loginPage.login();
    }
}

package ru.tki;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.BuildingType;
import ru.tki.models.ResearchType;
import ru.tki.models.StationType;
import ru.tki.models.actions.Action;
import ru.tki.models.tasks.ResearchTask;
import ru.tki.models.tasks.ResourceTask;
import ru.tki.models.tasks.StationTask;
import ru.tki.po.BasePage;
import ru.tki.po.LoginPage;
import ru.tki.po.ResourcesPage;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class BasicTest {

    private static Logger logger = Logger.getLogger(String.valueOf(DriverManager.class));

    DriverManager driverManager;
    BotConfigMain config;

    Navigation navigation;
    LoginPage loginPage;
    BasePage basePage;
    ResourcesPage resourcesPage;

    @Before
    public void setUp() throws Exception {
        BotConfigMainReader reader = new BotConfigMainReader();
        config = reader.getPropValues();
        ContextHolder.setBotConfigMain(config);

        driverManager = new DriverManager();
        ContextHolder.setDriverManager(driverManager);

        navigation = new Navigation();
        loginPage = new LoginPage();
        basePage = new BasePage();
        resourcesPage = new ResourcesPage();
    }

    @After
    public void tearDown() throws Exception {
        driverManager.closeDriver();
    }

    @Test
    public void testLogin() throws Exception {
        navigation.openHomePage();
        loginPage.login();
    }

    @Test
    public void testResources() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        logger.info("Current Metal amount: " + basePage.resources.getMetal());
        assertTrue(basePage.resources.getMetal() > 0);
        logger.info("Current Crystal amount: " + basePage.resources.getCrystal());
        assertTrue(basePage.resources.getCrystal() > 0);
        logger.info("Current Deuterium amount: " + basePage.resources.getDeuterium());
        assertTrue(basePage.resources.getDeuterium() > 0);
        logger.info("Current Energy amount: " + basePage.resources.getEnergy());
        assertTrue(basePage.resources.getEnergy() > 0);
    }

    @Test
    public void testLeftMenu() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        basePage.leftMenu.openResources();
        basePage.leftMenu.openOverview();
        basePage.leftMenu.openStation();
        basePage.leftMenu.openTraderOverview();
        basePage.leftMenu.openResearch();
        basePage.leftMenu.openShipyard();
        basePage.leftMenu.openDefense();
        basePage.leftMenu.openFleet();
        basePage.leftMenu.openGalaxy();
    }

    @Test
    public void testMyPlanets() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        for (AbstractPlanet planet : planets) {
            basePage.myWorlds.selectPlanet(planet);
        }
    }

    @Test
    public void buildSolarPlant() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        navigation.openResources();

        resourcesPage.build(BuildingType.SOLAR_PLANT);
    }

    @Test
    public void buildMetalMineByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        ResourceTask task = new ResourceTask(planets.get(0), BuildingType.METAL_MINE);
        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void buildRobotsFactoryByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        StationTask task = new StationTask(planets.get(0), StationType.ROBOTS_FACTORY);
        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void researchEspionageByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        ResearchTask task = new ResearchTask(planets.get(0), ResearchType.ESPIONAGE);
        Action action = task.execute();

        logger.info(action.toString());
    }
}

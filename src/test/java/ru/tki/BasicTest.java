package ru.tki;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ru.tki.executor.Navigation;
import ru.tki.models.*;
import ru.tki.models.actions.Action;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.*;
import ru.tki.models.types.*;
import ru.tki.po.*;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

@Ignore
public class BasicTest {

    private static Logger logger = Logger.getLogger(String.valueOf(DriverManager.class));

    DriverManager driverManager;
    BotConfigMain config;
    Empire empire;

    Navigation navigation;
    LoginPage  loginPage;
    BasePage   basePage;

    OverviewPage     overviewPage;
    BuildingsPage    buildingsPage;
    FactoriesPage    factoriesPage;
    ResearchesPage   researchesPage;
    DefencePage      defencePage;
    FleetPage        fleetPage;
    FleetDetailsPage fleetDetailsPage;

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

        overviewPage = new OverviewPage();
        buildingsPage = new BuildingsPage();
        factoriesPage = new FactoriesPage();
        researchesPage = new ResearchesPage();
        defencePage = new DefencePage();
        fleetPage = new FleetPage();
        fleetDetailsPage = new FleetDetailsPage();

        empire = new Empire();
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
        basePage.leftMenu.openFactory();
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
    public void testMyFirstPlanet() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();
        Planet planet = (Planet) planets.get(0);

        navigation.openOverview();
        planet.setName(overviewPage.getPlanetName());
        planet.setSize(overviewPage.getPlanetSize());

        navigation.openResources();
        planet.setBuildings(buildingsPage.getBuildings());

        navigation.openFactory();
        planet.setFactories(factoriesPage.getFactories());

        navigation.openResearch();
        planet.setResearches(researchesPage.getResearches());

        navigation.openDefense();
        planet.setDefence(defencePage.getDefence());

        navigation.openFleet();
        planet.setFleet(fleetPage.getFleet());

        logger.info(planet.toString());
    }

    @Test
    public void buildSolarPlant() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        navigation.openResources();

        buildingsPage.build(BuildingType.SOLAR_PLANT);
    }

    @Test
    public void buildMetalMineByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        BuildingTask task = new BuildingTask(new Empire(), planets.get(0), BuildingType.METAL_MINE);
        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void buildRobotsFactoryByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        FactoryTask task = new FactoryTask(new Empire(), planets.get(0), FactoryType.ROBOTS_FACTORY);
        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void researchEspionageByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        ResearchTask task = new ResearchTask(new Empire(), planets.get(0), ResearchType.ESPIONAGE);
        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void buildLightFighterByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        ShipyardTask task = new ShipyardTask(new Empire(), planets.get(0), ShipType.LIGHT_FIGHTER, 2);
        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void buildLightLaserByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        DefenceTask task = new DefenceTask(empire, planets.get(0), DefenceType.LIGHT_LASER, 3);
        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void sendFleetByTask() throws Exception {
        navigation.openHomePage();
        loginPage.checkLogin();

        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        FleetTask task = new FleetTask(
                new Empire(),
                planets.get(0),
                new Planet("2:39:6"),
                new Fleet(ShipType.LIGHT_FIGHTER, 1),
                MissionType.ATTACK,
                new Resources(1, 2, 3));
        task.setFleetSpeed(FleetSpeed.S80);

        Action action = task.execute();

        logger.info(action.toString());
    }

    @Test
    public void testRevertFleet() {
        navigation.openHomePage();
        loginPage.checkLogin();

        navigation.openFleetMove();
        List<FleetAction> fleetActions = fleetDetailsPage.getFleetActions(empire);

        fleetDetailsPage.getRevertDuration(fleetActions.get(0));

    }

    @Test
    public void testSpy() {
        navigation.openHomePage();
        loginPage.checkLogin();

        basePage.openMessages();

        MessagesPage messagesPage = new MessagesPage();

        List<Planet> i = messagesPage.parseSpyReports();
        System.out.println(i);
    }
}

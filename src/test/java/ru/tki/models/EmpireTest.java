package ru.tki.models;

import org.junit.Before;
import org.junit.Test;
import ru.tki.BotConfigMain;
import ru.tki.BotConfigMainReader;
import ru.tki.ContextHolder;
import ru.tki.DriverManager;

import java.util.logging.Logger;


public class EmpireTest {

    private static Logger logger = Logger.getLogger(String.valueOf(EmpireTest.class));

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

    @Test
    public void testSave() throws Exception {
        Empire empire = new Empire();
        Planet planet = new Planet("2:3:5", "Planet name");
        empire.addPlanet(planet);
        Planet planet1 = new Planet("1:214:9", "Second planet name");
        empire.addPlanet(planet1);

        empire.save();
    }

    @Test
    public void testLoad() throws Exception {
        Empire empire = new Empire();
        empire.load();
        empire.getPlanets();
    }
}
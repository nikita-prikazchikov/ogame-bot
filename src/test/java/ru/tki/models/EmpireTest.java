package ru.tki.models;

import org.junit.Before;
import org.junit.Test;
import ru.tki.BotConfigMain;
import ru.tki.BotConfigMainReader;
import ru.tki.ContextHolder;
import ru.tki.DriverManager;

import java.time.Instant;
import java.util.logging.Logger;


public class EmpireTest {

    private static Logger logger = Logger.getLogger(String.valueOf(EmpireTest.class));

    DriverManager driverManager;
    BotConfigMain config;
    Empire empire;

    @Before
    public void setUp() throws Exception {
        BotConfigMainReader reader = new BotConfigMainReader();
        config = reader.getPropValues();
        config.setLogin("test");
        config.setPassword("test");
        ContextHolder.setBotConfigMain(config);

        driverManager = new DriverManager();
        ContextHolder.setDriverManager(driverManager);

        empire = new Empire();
    }

    @Test
    public void testSave() throws Exception {
        Planet planet = new Planet("2:3:5", "Planet name");
        empire.addPlanet(planet);
        Planet planet1 = new Planet("1:214:9", "Second planet name");
        empire.addPlanet(planet1);

        empire.save();
    }

    @Test
    public void testLoad() throws Exception {
        empire.load();
        empire.getPlanets();
    }

    @Test
    public void testFormats() throws Exception {
        System.out.println(Instant.now());
    }


}
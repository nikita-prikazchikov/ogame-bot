package ru.tki.models;

import org.junit.Before;
import org.junit.Test;
import ru.tki.BotConfigMain;
import ru.tki.BotConfigMainReader;
import ru.tki.ContextHolder;


public class GalaxyTest {

    BotConfigMain config;
    Empire        empire;
    Galaxy        galaxy;

    @Before
    public void setUp() throws Exception {
        BotConfigMainReader reader = new BotConfigMainReader();
        config = reader.getPropValues();
        config.LOGIN = "test";
        config.PASSWORD = "test";
        ContextHolder.setBotConfigMain(config);

        empire = new Empire(config);
        galaxy = empire.getGalaxy();
    }

    @Test
    public void testHasPlanet() throws Exception {
        galaxy.addPlanet(new EnemyPlanet("2:5:4"));

        System.out.println(galaxy.hasPlanet(new EnemyPlanet("2:5:4")));
        System.out.println(galaxy.hasPlanet(new Planet("2:5:4")));
        System.out.println(galaxy.hasPlanet(new Moon("2:5:4")));
    }
}
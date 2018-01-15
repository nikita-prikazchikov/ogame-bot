package ru.tki.models;

import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

public class CoordinatesTest {
    private static Logger logger = Logger.getLogger(String.valueOf(CoordinatesTest.class));

    @Test
    public void testSimpleString(){
        Coordinates c = new Coordinates("2:45:7");
        assertEquals(c.getGalaxy(), "2");
        assertEquals(c.getSystem(), "45");
        assertEquals(c.getPlanet(), "7");
        logger.info(c.getGalaxy());
        logger.info(c.getSystem());
        logger.info(c.getPlanet());
    }

    @Test
    public void testStringBrackets(){
        Coordinates c = new Coordinates("[3:498:15]");
        assertEquals(c.getGalaxy(), "3");
        assertEquals(c.getSystem(), "498");
        assertEquals(c.getPlanet(), "15");
        logger.info(c.getGalaxy());
        logger.info(c.getSystem());
        logger.info(c.getPlanet());
    }

    @Test
    public void testStringEmpty(){
        Coordinates c = new Coordinates("  [1:2:3]  ");
        assertEquals(c.getGalaxy(), "1");
        assertEquals(c.getSystem(), "2");
        assertEquals(c.getPlanet(), "3");
        logger.info(c.getGalaxy());
        logger.info(c.getSystem());
        logger.info(c.getPlanet());
    }

    @Test
    public void testFormat(){
        Coordinates c = new Coordinates("  [1:2:3]  ");
        assertEquals(c.getGalaxy(), "1");
        assertEquals(c.getSystem(), "2");
        assertEquals(c.getPlanet(), "3");
        assertEquals(c.getFormattedCoordinates(), "1:2:3");

    }
}
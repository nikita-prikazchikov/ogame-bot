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

    @Test
    public void testEquals(){
        Coordinates base = new Coordinates("2:100:5");
        assertTrue(base.equals(base));
        assertTrue(base.equals(new Coordinates("2:100:5")));
        assertFalse(base.equals(new Coordinates("2:101:5")));
    }

    @Test
    public void testCloser(){
        Coordinates base = new Coordinates("2:100:5");
        assertTrue(new Coordinates("2:81:5").equals(base.closer(new Coordinates("2:80:15"), new Coordinates("2:81:5"))));
        assertTrue(new Coordinates("1:81:5").equals(base.closer(new Coordinates("4:80:15"), new Coordinates("1:81:5"))));
        assertTrue(new Coordinates("1:81:5").equals(base.closer(new Coordinates("1:81:15"), new Coordinates("1:81:5"))));
    }

}
package ru.tki.models;

import ru.tki.executor.Navigation;
import ru.tki.po.GalaxyPage;

import java.util.List;
import java.util.logging.Logger;

public class Galaxy {
    private static Logger  logger     = Logger.getLogger(String.valueOf(Galaxy.class));
    private static Integer MIN_SYSTEM = 1;
    private static Integer MAX_SYSTEM = 499;
    private static Integer STEP       = 10;

    private Integer systemLeft;
    private Integer systemRight;

    private GalaxyPage galaxyPage;

    public AbstractPlanet findNewColony(AbstractPlanet planet) {
        Navigation navigation = new Navigation();
        galaxyPage = new GalaxyPage();

        navigation.selectPlanet(planet);
        navigation.openGalaxy();

        Integer galaxy = planet.getCoordinates().getGalaxy();
        Integer system = planet.getCoordinates().getSystem();

        while (galaxy < 8) {

            systemLeft = system;
            systemRight = system + 1;

            Planet newColony;
            while (systemLeft > MIN_SYSTEM && systemRight < MAX_SYSTEM) {
                newColony = moveLeft();
                if (null != newColony) {
                    return newColony;
                }
                newColony = moveRight();
                if (null != newColony) {
                    return newColony;
                }
            }
            galaxy++;
            galaxyPage.selectGalaxy(galaxy);
        }

        return null;
    }

    private Planet moveLeft() {
        int stop = Math.max(systemLeft - STEP, MIN_SYSTEM);
        if (systemLeft > stop && Integer.parseInt(galaxyPage.getSystem()) != systemLeft) {
            galaxyPage.selectSystem(systemLeft);
        }
        while (systemLeft > stop) {
            Planet planet = getGoodPlanet(galaxyPage.getEmptyPlanets());
            if (planet != null) {
                return planet;
            }
            galaxyPage.prevSystem();
            systemLeft--;
        }
        return null;
    }

    private Planet moveRight() {
        int stop = Math.min(systemRight + STEP, MAX_SYSTEM);
        if (systemRight < stop && Integer.parseInt(galaxyPage.getSystem()) != systemRight) {
            galaxyPage.selectSystem(systemRight);
        }
        while (systemRight < stop) {
            Planet planet = getGoodPlanet(galaxyPage.getEmptyPlanets());
            if (planet != null) {
                return planet;
            }
            galaxyPage.nextSystem();
            systemRight++;
        }
        return null;
    }

    private Planet getGoodPlanet(List<Planet> planets) {
        for (Planet planet : planets) {
            int position = planet.getCoordinates().getPlanet();
            if (position > 5 && position < 11) {
                return planet;
            }
        }
        System.out.printf("No good planet for colonization in: %s:%s%n", galaxyPage.getGalaxy(), galaxyPage.getSystem());
        return null;
    }
}

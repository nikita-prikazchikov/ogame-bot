package ru.tki.models;

import ru.tki.executor.Navigation;
import ru.tki.po.GalaxyPage;

import java.util.List;
import java.util.logging.Logger;

public class GalaxyHelper {
    private static Logger  logger     = Logger.getLogger(String.valueOf(GalaxyHelper.class));
    private static Integer STEP       = 3;

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
            while (systemLeft > Galaxy.MIN_SYSTEM && systemRight < Galaxy.MAX_SYSTEM) {
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
        int stop = Math.max(systemLeft - STEP, Galaxy.MIN_SYSTEM);
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
        int stop = Math.min(systemRight + STEP, Galaxy.MAX_SYSTEM);
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
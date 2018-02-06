package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.po.GalaxyPage;

import java.util.Iterator;
import java.util.List;

//Task for rescan inactive players to check they resources
public class RescanInactivePlayersTask extends AbstractGalaxyScanTask {

    List<EnemyPlanet> planets;

    public RescanInactivePlayersTask(Empire empire, AbstractPlanet planet, List<EnemyPlanet> planets) {
        super(empire);
        setPlanet(planet);
        this.planets = planets;
    }

    @Override
    public FleetAction execute() {
        super.execute();
        Navigation navigation = new Navigation();
        GalaxyPage galaxyPage = new GalaxyPage();

        navigation.selectPlanet(getPlanet());

        int activeFleets = empire.getActiveFleets();
        int maxSpy = Math.min(Math.min(empire.getMaxFleets() - activeFleets, 8), getPlanet().getFleet().getEspionageProbe());
        System.out.println(String.format("Maximum can send: %s spies", maxSpy));

        Iterator<EnemyPlanet> iter = planets.iterator();

        int spies = 0;
        while (spies < maxSpy && iter.hasNext()) {
            EnemyPlanet planet = iter.next();
            navigation.openGalaxy();
            galaxyPage.findPlanet(planet);
            if (galaxyPage.isEmpty(planet) || !galaxyPage.isInactive(planet)) {
                System.out.println(String.format("Planet %s is not inactive or doesn't exist anymore", planet.getCoordinates()));
                galaxy.removePlanet(planet);
            } else {
                galaxyPage.sendSpy(planet);
                galaxyPage.pause(1000);
                spies++;
            }
        }
        waitActiveFleets(activeFleets);
        readMessages();
        return null;
    }

    @Override
    public String toString() {
        return "Rescan enemy planets to find best attack target: " + planets.stream().map((planet) -> planet.getCoordinates().toString()).reduce(String::concat);
    }
}

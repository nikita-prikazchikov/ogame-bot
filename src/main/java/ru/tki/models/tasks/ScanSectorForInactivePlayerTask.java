package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.po.GalaxyPage;

import java.util.List;

//Task for rescan of sector for new inactive players. Known (I)(i) planets will not be rescanned here
public class ScanSectorForInactivePlayerTask extends AbstractGalaxyScanTask {

    private GalaxySector sector;

    public ScanSectorForInactivePlayerTask(Empire empire, AbstractPlanet planet, GalaxySector sector) {
        super(empire);
        setPlanet(planet);
        this.sector = sector;
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

        Coordinates current = sector.getStart();
        Integer currentPlanet = 0;
        Coordinates end = sector.getEnd();

        if (maxSpy < 3) {
            return null;
        }
        while (current.getSystem() <= end.getSystem()) {
            navigation.openGalaxy();
            System.out.println("Scan from system: " + current);
            galaxyPage.findPlanet(current);
            int spies = 0;

            boolean completed = true;
            do {
                List<Planet> inactivePlanets = galaxyPage.getInactivePlanets();
                for (Planet planet : inactivePlanets) {
                    if (planet.getCoordinates().getPlanet() <= currentPlanet
                            || galaxy.hasPlanet(planet)) {
                        continue;
                    }
                    if (spies < maxSpy) {
                        galaxyPage.sendSpy(planet);
                        galaxyPage.pause(1000);
                        currentPlanet = planet.getCoordinates().getPlanet();
                        spies++;
                    } else {
                        completed = false;
                        break;
                    }
                }
                if (completed) {
                    current = current.nextSystem();
                    galaxyPage.nextSystem();
                    currentPlanet = 0;
                    if (current.getSystem() >= Galaxy.MAX_SYSTEM || current.getSystem() > end.getSystem()) {
                        break;
                    }
                }
            } while (completed);

            waitActiveFleets(activeFleets);
            readMessages();
        }

        galaxy.updateSector(sector);
        return null;
    }

    @Override
    public String toString() {
        return String.format("Scan sector from %s to %s for new inactive players", sector.getStart(), sector.getEnd());
    }
}

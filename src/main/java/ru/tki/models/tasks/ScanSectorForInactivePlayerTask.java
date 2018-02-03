package ru.tki.models.tasks;

import org.openqa.selenium.StaleElementReferenceException;
import ru.tki.ContextHolder;
import ru.tki.executor.Navigation;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.po.BasePage;
import ru.tki.po.FleetDetailsPage;
import ru.tki.po.GalaxyPage;
import ru.tki.po.MessagesPage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

//Task for rescan of sector for new inactive players. Known I will not be rescanned here
public class ScanSectorForInactivePlayerTask extends Task {

    private Empire empire;
    private Galaxy galaxy;
    private AbstractPlanet planet;
    private GalaxySector   sector;


    public ScanSectorForInactivePlayerTask(Empire empire, AbstractPlanet planet, GalaxySector sector) {
        this.empire = empire;
        this.galaxy = empire.getGalaxy();
        this.planet = planet;
        this.sector = sector;
    }

    @Override
    public FleetAction execute() {
        Navigation navigation = new Navigation();
        GalaxyPage galaxyPage = new GalaxyPage();

        navigation.selectPlanet(planet);

        int activeFleets = empire.getActiveFleets();
        int maxSpy = Math.min(Math.min(empire.getMaxFleets() - activeFleets, 8), planet.getFleet().getEspionageProbe());
        System.out.println(String.format("Maximum can send: %s spies", maxSpy));

        Coordinates current = sector.getStart();
        Integer currentPlanet = 0;
        Coordinates end = sector.getEnd();

        while (current.getSystem() <= end.getSystem()) {
            navigation.openGalaxy();
            System.out.println("Scan from system: " + current );
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

    private void waitActiveFleets(int active) {
        Navigation navigation = new Navigation();
        FleetDetailsPage fleetPage = new FleetDetailsPage();

        System.out.println("Spies are sent. Wait them to return");
        Instant finishTime = Instant.now().plus(Duration.ofMinutes(ContextHolder.getBotConfigMain().ATTACK_CHECK_TIMEOUT));
        do {
            if (Instant.now().compareTo(finishTime) > 0) {
                break;
            }
            navigation.openFleetMove();
            try {
                if (fleetPage.getActiveFleets() > active) {
                    fleetPage.pause(ContextHolder.getBotConfigMain().SLEEP_TIMEOUT);
                    System.out.print("-");
                } else break;
            }catch (StaleElementReferenceException ignored){}
        } while (true);
    }

    private void readMessages() {
        System.out.println("");
        System.out.println("Read new spy reports");
        BasePage basePage = new BasePage();
        basePage.openMessages();
        MessagesPage messagesPage = new MessagesPage();
        messagesPage.parseSpyReports().forEach(planet1 -> galaxy.addPlanet(planet1));
    }

    @Override
    public String toString() {
        return String.format("Scan sector from %s to %s for new inactive players", sector.getStart(), sector.getEnd());
    }
}

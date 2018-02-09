package ru.tki.models.tasks;

import org.openqa.selenium.StaleElementReferenceException;
import ru.tki.ContextHolder;
import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.Galaxy;
import ru.tki.po.BasePage;
import ru.tki.po.FleetDetailsPage;
import ru.tki.po.MessagesPage;

import java.time.Duration;
import java.time.Instant;

public class AbstractGalaxyScanTask extends Task {
    protected Empire empire;
    protected Galaxy galaxy;

    public AbstractGalaxyScanTask(Empire empire) {
        this.empire = empire;
        this.galaxy = empire.getGalaxy();
        setPriority(LOW);
    }

    protected void waitActiveFleets(int active) {
        Navigation navigation = new Navigation();
        FleetDetailsPage fleetPage = new FleetDetailsPage();

        System.out.println("Spies are sent. Wait them to return");
        Instant finishTime = Instant.now().plus(Duration.ofSeconds(ContextHolder.getBotConfigMain().ATTACK_CHECK_TIMEOUT));
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

    protected void readMessages() {
        System.out.println("");
        System.out.println("Read new spy reports");
        BasePage basePage = new BasePage();
        basePage.openMessages();
        MessagesPage messagesPage = new MessagesPage();
        messagesPage.parseSpyReports().forEach(planet1 -> galaxy.addPlanet(planet1));
    }
}

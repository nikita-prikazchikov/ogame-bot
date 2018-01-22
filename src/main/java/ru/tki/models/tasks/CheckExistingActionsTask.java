package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.BasePage;
import ru.tki.po.OverviewPage;

public class CheckExistingActionsTask extends Task {

    Empire empire;

    public CheckExistingActionsTask(Empire empire) {
        this.empire = empire;
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openOverview();
        BasePage basePage = new BasePage();
        OverviewPage overviewPage = new OverviewPage();

        Action action = overviewPage.getResearchAction(empire, empire.getPlanets().get(0));
        if (null != action) {
            empire.addAction(action);
        }

        for (AbstractPlanet planet : empire.getPlanets()) {
            navigation.selectPlanet(planet);
            planet.setResources(basePage.resources.getResources());

            action = overviewPage.getBuildAction(planet);
            if (null != action) {
                empire.addAction(action);
            }

            action = overviewPage.getShipyardAction(planet);
            if (null != action) {
                empire.addAction(action);
            }

        }
        return null;
    }

    @Override
    public String toString() {
        return "Check existing buildings and researches";
    }
}
